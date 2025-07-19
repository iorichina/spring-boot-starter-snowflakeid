package iorichina.springboot.starter.snowflakeid;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Component
public class SnowFlakeIdHelper {
    private final long startTime;
    private final boolean timeInMillis;
    private final long zoneId;
    private final long nodeId;

    private final long bitsOfTime;
    private final long bitsOfZone;
    private final long bitsOfNode;
    private final long bitsOfAutoincrementMax;

    private final long maxTimeNum;
    private final long maxZoneNum;
    private final long maxNodeNum;
    private final long maxAutoincrementNum;

    private final long leftOfNode;
    private final long leftOfZone;
    private final long leftOfTime;

    private final RecyclableAtomicLong sequence;
    private final ZoneOffset offset;

    public SnowFlakeIdHelper(LocalDateTime startTime, boolean timeInMillis, long zoneId, long nodeId, long bitsOfTime, long bitsOfZone, long bitsOfNode, long bitsOfAutoincrementMax, int recyclableLongMaxTry) {
        offset = OffsetDateTime.now().getOffset();
        this.startTime = timeInMillis ? startTime.toInstant(offset).toEpochMilli() : startTime.toEpochSecond(offset);
        this.timeInMillis = timeInMillis;
        this.bitsOfTime = bitsOfTime;
        this.bitsOfZone = bitsOfZone;
        this.bitsOfNode = bitsOfNode;
        this.bitsOfAutoincrementMax = bitsOfAutoincrementMax;

        maxTimeNum = -1L ^ (-1L << bitsOfTime);
        maxZoneNum = -1L ^ (-1L << bitsOfZone);
        maxNodeNum = -1L ^ (-1L << bitsOfNode);
        maxAutoincrementNum = -1L ^ (-1L << bitsOfAutoincrementMax);

        this.zoneId = zoneId % maxZoneNum;
        this.nodeId = nodeId % maxNodeNum;

        leftOfNode = bitsOfAutoincrementMax;
        leftOfZone = bitsOfAutoincrementMax + bitsOfNode;
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
        if (this.timeInMillis) {
            return genId(localDateTime.toInstant(offset).toEpochMilli(), sequence);
        }
        return genId(localDateTime.toEpochSecond(offset), sequence);
    }

    /**
     * generate next ID with special time
     *
     * @param timeInMillis time in milliseconds since epoch in local timezone
     */
    public long genId(long timeInMillis) {
        long sequence = this.sequence.getAndIncrementWithRecycle();
        if (this.timeInMillis) {
            return genId(timeInMillis, sequence);
        }
        return genId(timeInMillis / 1000, sequence);
    }

    /**
     * generate next ID with special time, zoneId and nodeId
     */
    private long genId(long time, long sequence) {
        return ((time - startTime) << leftOfTime)
                | (zoneId << leftOfZone)
                | (nodeId << leftOfNode)
                | (sequence % maxAutoincrementNum);
    }

    /**
     * parse timestamp from ID
     */
    public long parseTimeMillis(long id) {
        return startTime + (id >>> leftOfTime);
    }

}
