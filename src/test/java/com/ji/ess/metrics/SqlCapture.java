package com.ji.ess.metrics;

import java.util.ArrayList;
import java.util.List;

public class SqlCapture {
    private static final ThreadLocal<List<String>> QUERIES = ThreadLocal.withInitial(ArrayList::new);

    public void clear() {
        QUERIES.get().clear();
    }

    public void add(String sql) {
        if (sql == null) return;
        String normalized = sql.replaceAll("\\s+", " ").trim();
        if (normalized.isBlank()) return;

        String lower = normalized.toLowerCase();
        if (!(lower.startsWith("select")
                || lower.startsWith("insert")
                || lower.startsWith("update")
                || lower.startsWith("delete"))) {
            return;
        }

        QUERIES.get().add(normalized);
    }

    public List<String> snapshot() {
        return List.copyOf(QUERIES.get());
    }
}

