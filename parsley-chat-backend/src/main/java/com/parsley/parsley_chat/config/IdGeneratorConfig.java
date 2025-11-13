package com.parsley.parsley_chat.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfig {

    // These two variables are for generate snowflake primary key
    // The value for now just arbitrarily
    private final Long workerId = 2L;
    private final Long datacenterId = 1L;

    /**
     * Exposes a singleton Snowflake generator:
     * 41 bits timestamp, 5 bits datacenter, 5 bits worker, 12 bits sequence
     */
    @Bean
    public Snowflake snowflake() {
        return IdUtil.getSnowflake(workerId, datacenterId);
    }

}
