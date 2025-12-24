package com.ji.ess.global.monitoring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QueryMetricsProperties.class)
public class QueryMetricsConfig {
}

