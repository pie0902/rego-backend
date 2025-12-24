package com.ji.ess.global.monitoring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "metrics.query")
public record QueryMetricsProperties(Map<String, Integer> baseline) {
}

