package iorichina.springboot.starter.snowflakeid;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Getter
public class SnowFlakeIdHelper {
    private final long startTime;
    private final TimeUnit unit;
    private final long zoneId;
    private final long nodeId;

    /**
     * recommended bits of snowflake id:
     * ------------------
     * sign bit(1)+
     * time bits(default 40 with max 34 years in millis)+
     * zone bits(default 3 bits with max value 7)+
     * node bit(default 8 bits with max value 255)+
     * autoincrement(default 12 bits with max value 4095)
     * ------------------ or
     * sign bit(1)+
     * time bits(default 31 with max 68 years in seconds)+
     * zone bits(default 3 bits with max value 7)+
     * node bit(default 8 bits with max value 255)+
     * autoincrement(default 21 bits with max value 2,097,151)
     */
    private final long bitsOfTime;
    private final long bitsOfZone;
    private final long bitsOfNode;
    private final long bitsOfAutoincrement;

    private final long maxTimeNum;
    private final long maxZoneNum;
    private final long maxNodeNum;
    private final long maxAutoincrementNum;

    private final long leftOfNode;
    private final long leftOfZone;
    private final long leftOfTime;

    private final RecyclableAtomicLong sequence;
    private final ZoneOffset offset;

    public SnowFlakeIdHelper(LocalDateTime startTime, TimeUnit unit, long zoneId, long nodeId, long bitsOfTime, long bitsOfZone, long bitsOfNode, long bitsOfAutoincrement, int recyclableLongMaxTry) {
        this.offset = OffsetDateTime.now().getOffset();
        long timeInMillis = startTime.toInstant(offset).toEpochMilli();
        this.startTime = unit.convert(timeInMillis, TimeUnit.MILLISECONDS);
        this.unit = unit;
        this.bitsOfTime = bitsOfTime;
        this.bitsOfZone = bitsOfZone;
        this.bitsOfNode = bitsOfNode;
        this.bitsOfAutoincrement = bitsOfAutoincrement;

        maxTimeNum = -1L ^ (-1L << bitsOfTime);
        maxZoneNum = -1L ^ (-1L << bitsOfZone);
        maxNodeNum = -1L ^ (-1L << bitsOfNode);
        maxAutoincrementNum = -1L ^ (-1L << bitsOfAutoincrement);

        this.zoneId = zoneId % maxZoneNum;
        this.nodeId = nodeId % maxNodeNum;

        leftOfNode = bitsOfAutoincrement;
        leftOfZone = leftOfNode + bitsOfNode;
        leftOfTime = leftOfZone + bitsOfZone;

        this.sequence = new RecyclableAtomicLong(maxAutoincrementNum, recyclableLongMaxTry);
    }

    /**
     * generate next ID
     */
    public long genId() {
        return genId(System.currentTimeMillis());
    }

    /**
     * generate next ID with special time
     */
    public long genId(LocalDateTime localDateTime) {
        long sequence = this.sequence.getAndIncrementWithRecycle();
        return genId(localDateTime.toInstant(offset).toEpochMilli(), sequence);
    }

    /**
     * generate next ID with special time
     *
     * @param timeInMillis time in milliseconds since epoch in local timezone
     */
    public long genId(long timeInMillis) {
        long sequence = this.sequence.getAndIncrementWithRecycle();
        return genId(unit.convert(timeInMillis, TimeUnit.MILLISECONDS), sequence);
    }

    /**
     * generate next ID with special time, zoneId and nodeId
     */
    private long genId(long time, long sequence) {
        return ((time - startTime) << leftOfTime)
                | (zoneId << leftOfZone)
                | (nodeId << leftOfNode)
                | (sequence & maxAutoincrementNum);
    }

    /**
     * parse timestamp from ID
     */
    public long parseTimeInMillis(long id) {
        return unit.toMillis(startTime + (id >>> leftOfTime));
    }

    /**
     * parse localdatetime from ID
     */
    public LocalDateTime parseTime(long id) {
        long nanos = unit.toNanos(startTime + (id >>> leftOfTime));
        return LocalDateTime.ofEpochSecond(TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS),
                (int) (nanos % 1_000_000_000), offset);
    }

}
