package com.ji.ess.metrics;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SqlCaptureTestConfig {

    @Bean
    public SqlCapture sqlCapture() {
        return new SqlCapture();
    }

    @Bean
    public SqlCaptureStatementInspector sqlCaptureStatementInspector(SqlCapture sqlCapture) {
        return new SqlCaptureStatementInspector(sqlCapture);
    }

    @Bean
    public HibernatePropertiesCustomizer sqlCaptureHibernateCustomizer(SqlCaptureStatementInspector inspector) {
        return hibernateProperties -> hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, inspector);
    }
}

