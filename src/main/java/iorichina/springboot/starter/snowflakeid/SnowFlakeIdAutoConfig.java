package iorichina.springboot.starter.snowflakeid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(SnowFlakeIdProperties.class)
public class SnowFlakeIdAutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public SnowFlakeIdHelper snowFlakeIdHelper(SnowFlakeIdProperties properties) {
        LocalDateTime startTime = LocalDateTime.parse(properties.getStartTime());
        TimeUnit unit = switch (properties.getTimeUnit()) {
            case "NANOSECONDS" -> TimeUnit.NANOSECONDS;
            case "MICROSECONDS" -> TimeUnit.MICROSECONDS;
            case "SECONDS" -> TimeUnit.SECONDS;
            case "MINUTES" -> TimeUnit.MINUTES;
            case "HOURS" -> TimeUnit.HOURS;
            case "DAYS" -> TimeUnit.DAYS;
            default -> TimeUnit.MILLISECONDS;
        };
        long zoneId = properties.getTenantId();
        long nodeId = properties.getNodeId();
        long bitsOfTime = properties.getBitsOfTime();
        long bitsOfZone = properties.getBitsOfTenant();
        long bitsOfNode = properties.getBitsOfNode();
        long bitsOfAutoincrementMax = properties.getBitsOfAutoincrement();

        return new SnowFlakeIdHelper(startTime, unit, zoneId, nodeId, bitsOfTime, bitsOfZone, bitsOfNode, bitsOfAutoincrementMax, properties.getRecyclableLongMaxTry());
    }
}
