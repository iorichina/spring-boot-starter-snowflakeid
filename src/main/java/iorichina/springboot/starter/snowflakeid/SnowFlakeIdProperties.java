package iorichina.springboot.starter.snowflakeid;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "snowflakeid")
public class SnowFlakeIdProperties {
    /**
     * CAS try times while recyclable long fail reset to 0, use synchronized lock after max try
     */
//    @Value("${snowflakeid.recyclable-long-max-try:100}")
    private int recyclableLongMaxTry = 1000;

    /**
     * start time of snowflake id, default to 2025-07-19T00:00:00
     */
//    @Value("${snowflakeid.start-time:2025-07-19T00:00:00}")
    private String startTime = "2025-07-19T00:00:00";
    /**
     * NANOSECONDS
     * MICROSECONDS
     * MILLISECONDS
     * SECONDS
     * MINUTES
     * HOURS
     * DAYS
     */
//    @Value("${snowflakeid.time-unit:MILLISECONDS}")
    private String timeUnit = "MILLISECONDS";
    /**
     * tenant id and node id are used to distinguish different machines in the same tenant
     */
//    @Value("${snowflakeid.tenant-id:0}")
    private long tenantId = 0;
    /**
     * default to 0, which means not set, and will be set to last 8 bits of local ipv4 address
     */
//    @Value("${snowflakeid.node-id:0}")
    private long nodeId = 0;

    /**
     * recommended bits of snowflake id:
     * ------------------
     * sign bit(1)+
     * time bits(default 40 with max 34 years in millis)+
     * tenant bits(default 3 bits with max value 7)+
     * node bit(default 8 bits with max value 255)+
     * autoincrement(default 12 bits with max value 4095)
     * ------------------ or
     * sign bit(1)+
     * time bits(default 31 with max 68 years in seconds)+
     * tenant bits(default 3 bits with max value 7)+
     * node bit(default 8 bits with max value 255)+
     * autoincrement(default 21 bits with max value 2,097,151)
     */
//    @Value("${snowflakeid.bits-of-time:40}")
    private int bitsOfTime = 40;
    //    @Value("${snowflakeid.bits-of-tenant:3}")
    private int bitsOfTenant = 3;
    //    @Value("${snowflakeid.bits-of-node:8}")
    private int bitsOfNode = 8;
    //    @Value("${snowflakeid.bits-of-autoincrement:12}")
    private int bitsOfAutoincrement = 12;
}
