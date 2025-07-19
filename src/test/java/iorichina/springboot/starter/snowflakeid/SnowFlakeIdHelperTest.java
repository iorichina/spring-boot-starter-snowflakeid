package iorichina.springboot.starter.snowflakeid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SnowFlakeIdHelperTest {
    @Test
    void testGenIdAndParseTimeMillis_seconds() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        SnowFlakeIdHelper helper = new SnowFlakeIdHelper(start, false, 1, 1, 32, 5, 5, 12, 10);
        long id = helper.genId();
        long timeMillis = helper.parseTimeMillis(id);
        long now = System.currentTimeMillis() / 1000; // 精度为秒
        Assertions.assertTrue(Math.abs(timeMillis - now) < 2000, "时间戳解析不正确");
    }

    @Test
    void testGenIdAndParseTimeMillis_millis() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        SnowFlakeIdHelper helper = new SnowFlakeIdHelper(start, true, 2, 3, 41, 4, 4, 10, 10);
        long id = helper.genId();
        long timeMillis = helper.parseTimeMillis(id);
        long now = System.currentTimeMillis();
        Assertions.assertTrue(Math.abs(timeMillis - now) < 1000, "时间戳解析不正确");
    }

    @Test
    void testGenIdWithCustomTime() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime custom = LocalDateTime.of(2024, 7, 19, 12, 0);
        SnowFlakeIdHelper helper = new SnowFlakeIdHelper(start, true, 0, 0, 41, 4, 4, 10, 10);
        long id = helper.genId(custom);
        long timeMillis = helper.parseTimeMillis(id);
        long expected = custom.toInstant(helper.getOffset()).toEpochMilli();
        Assertions.assertTrue(Math.abs(timeMillis - expected) < 10, "自定义时间戳解析不正确");
    }

    @Test
    void testDifferentConfigIdUniqueness() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        SnowFlakeIdHelper helper1 = new SnowFlakeIdHelper(start, true, 1, 1, 41, 4, 4, 10, 10);
        SnowFlakeIdHelper helper2 = new SnowFlakeIdHelper(start, true, 2, 2, 41, 4, 4, 10, 10);
        long id1 = helper1.genId();
        long id2 = helper2.genId();
        Assertions.assertNotEquals(id1, id2, "不同配置下ID应不同");
    }
}

