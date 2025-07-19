package iorichina.springboot.starter.snowflakeid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@EnableConfigurationProperties(SnowFlakeIdProperties.class)
public class SnowFlakeIdAutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public SnowFlakeIdHelper snowFlakeIdHelper(SnowFlakeIdProperties properties) {
        LocalDateTime startTime = LocalDateTime.parse(properties.getStartTime() == null ? "2025-07-19T00:00:00" : properties.getStartTime());
        boolean timeInMillis = properties.isTimeInMillis();
        long zoneId = properties.getZoneId();
        long nodeId = properties.getNodeId();
        long bitsOfTime = properties.getBitsOfTime();
        long bitsOfZone = properties.getBitsOfZone();
        long bitsOfNode = properties.getBitsOfNode();
        long bitsOfAutoincrementMax = properties.getBitsOfAutoincrementMax();

        return new SnowFlakeIdHelper(startTime, timeInMillis, zoneId, nodeId, bitsOfTime, bitsOfZone, bitsOfNode, bitsOfAutoincrementMax, properties.getRecyclableLongMaxTry());
    }
}
