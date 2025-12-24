package com.ji.ess.metrics;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class SqlCaptureStatementInspector implements StatementInspector {
    private final SqlCapture sqlCapture;

    public SqlCaptureStatementInspector(SqlCapture sqlCapture) {
        this.sqlCapture = sqlCapture;
    }

    @Override
    public String inspect(String sql) {
        sqlCapture.add(sql);
        return sql;
    }
}

