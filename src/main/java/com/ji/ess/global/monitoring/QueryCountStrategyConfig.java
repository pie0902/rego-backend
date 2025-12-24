package com.ji.ess.global.monitoring;

import net.ttddyy.dsproxy.listener.QueryCountStrategy;
import net.ttddyy.dsproxy.listener.ThreadQueryCountHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryCountStrategyConfig {

    @Bean
    public QueryCountStrategy queryCountStrategy() {
        return new ThreadQueryCountHolder();
    }
}

