package com.ji.ess.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QueryMetricsTestSupport {
    private static final int N_PLUS_ONE_SQL_THRESHOLD = 10;
    private static final double N_PLUS_ONE_REPEAT_RATE = 2.0;
    private static final int MAX_REPEAT_N_PLUS_ONE_HINT = 5;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final SqlCapture sqlCapture;

    public QueryMetricsTestSupport(MockMvc mockMvc, ObjectMapper objectMapper, SqlCapture sqlCapture) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.sqlCapture = sqlCapture;
    }

    public Map<String, Object> call(QueryScenario scenario, ScenarioTokens tokens) throws Exception {
        if (scenario.skip()) {
            return skippedRow(scenario);
        }

        sqlCapture.clear();
        MockHttpServletRequestBuilder requestBuilder = buildRequest(scenario);
        String token = tokens.resolve(scenario.auth());
        if (token != null && !token.isBlank()) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String queryCount = result.getResponse().getHeader("X-Query-Count");
        String queryTimeMs = result.getResponse().getHeader("X-Query-Time-Ms");
        String queryAvgTimeMs = result.getResponse().getHeader("X-Query-Avg-Time-Ms");

        List<String> sqlList = sqlCapture.snapshot();
        List<Map<String, Object>> queries = aggregateQueries(sqlList);

        int sqlCount = sqlList.size();
        int uniqueSqlCount = queries.size();
        double repeatRate = uniqueSqlCount > 0 ? (double) sqlCount / uniqueSqlCount : 0.0;
        int maxRepeat = maxRepeat(queries);
        boolean nPlusOneSuspect = (sqlCount >= N_PLUS_ONE_SQL_THRESHOLD && repeatRate >= N_PLUS_ONE_REPEAT_RATE)
                || maxRepeat >= MAX_REPEAT_N_PLUS_ONE_HINT;
        List<String> hints = buildHints(sqlList);

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("domain", scenario.domain());
        row.put("name", scenario.name());
        row.put("method", scenario.method());
        row.put("path", displayPath(scenario));
        row.put("status", result.getResponse().getStatus());
        row.put("queryCount", queryCount != null ? Long.parseLong(queryCount) : null);
        row.put("queryTimeMs", queryTimeMs != null ? Long.parseLong(queryTimeMs) : null);
        row.put("queryAvgTimeMs", queryAvgTimeMs);
        row.put("sqlCount", sqlCount);
        row.put("uniqueSqlCount", uniqueSqlCount);
        row.put("repeatRate", String.format(Locale.ROOT, "%.2f", repeatRate));
        row.put("maxRepeat", maxRepeat);
        row.put("nPlusOneSuspect", nPlusOneSuspect);
        row.put("hints", hints);
        row.put("queries", queries);
        return row;
    }

    public String toMarkdown(Map<String, Object> report) {
        Object rowsObj = report.get("rows");
        if (!(rowsObj instanceof List<?> list)) {
            return "# Query Metrics Report\n\n(no data)\n";
        }

        String generatedAt = String.valueOf(report.getOrDefault("generatedAt", ""));
        List<Map<String, Object>> normalizedRows = normalizeRows(list);
        Map<String, List<Map<String, Object>>> grouped = groupByDomain(normalizedRows);

        StringBuilder sb = new StringBuilder();
        sb.append("# Query Metrics Report\n\n");
        sb.append("- generatedAt: ").append(generatedAt).append('\n');
        sb.append('\n');
        sb.append("## Summary\n\n");
        sb.append("| domain | name | method | path | status | queryCount | queryTimeMs | avgMs | sqlCount | uniqueSql | repeatRate | maxRepeat | n+1 |\n");
        sb.append("|---|---|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---|\n");

        for (Map<String, Object> row : normalizedRows) {
            sb.append('|').append(s(row.get("domain"))).append(' ')
                    .append("| ").append(s(row.get("name")))
                    .append(" | ").append(s(row.get("method")))
                    .append(" | ").append(s(row.get("path")))
                    .append(" | ").append(n(row.get("status")))
                    .append(" | ").append(n(row.get("queryCount")))
                    .append(" | ").append(n(row.get("queryTimeMs")))
                    .append(" | ").append(s(row.get("queryAvgTimeMs")))
                    .append(" | ").append(n(row.get("sqlCount")))
                    .append(" | ").append(n(row.get("uniqueSqlCount")))
                    .append(" | ").append(s(row.get("repeatRate")))
                    .append(" | ").append(n(row.get("maxRepeat")))
                    .append(" | ").append(flag(row.get("nPlusOneSuspect")))
                    .append(" |\n");
        }

        sb.append('\n');
        sb.append("## Domains\n\n");

        for (Map.Entry<String, List<Map<String, Object>>> entry : grouped.entrySet()) {
            String domain = entry.getKey();
            List<Map<String, Object>> domainRows = entry.getValue();

            sb.append("<details>\n");
            sb.append("<summary>").append(domain).append(" (").append(domainRows.size()).append(")</summary>\n\n");

            sb.append("### Summary\n\n");
            sb.append("| name | method | path | status | queryCount | queryTimeMs | avgMs | sqlCount | uniqueSql | repeatRate | maxRepeat | n+1 |\n");
            sb.append("|---|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---|\n");
            for (Map<String, Object> row : domainRows) {
                sb.append('|').append(s(row.get("name"))).append(' ')
                        .append("| ").append(s(row.get("method")))
                        .append(" | ").append(s(row.get("path")))
                        .append(" | ").append(n(row.get("status")))
                        .append(" | ").append(n(row.get("queryCount")))
                        .append(" | ").append(n(row.get("queryTimeMs")))
                        .append(" | ").append(s(row.get("queryAvgTimeMs")))
                        .append(" | ").append(n(row.get("sqlCount")))
                        .append(" | ").append(n(row.get("uniqueSqlCount")))
                        .append(" | ").append(s(row.get("repeatRate")))
                        .append(" | ").append(n(row.get("maxRepeat")))
                        .append(" | ").append(flag(row.get("nPlusOneSuspect")))
                        .append(" |\n");
            }

            sb.append('\n');
            sb.append("### Details\n\n");

            for (Map<String, Object> row : domainRows) {
                sb.append("#### ").append(s(row.get("name"))).append('\n');
                sb.append("- ").append(s(row.get("method"))).append(' ').append(s(row.get("path")))
                        .append(" (status ").append(n(row.get("status"))).append(")\n");

                Object skipReason = row.get("skipReason");
                if (skipReason != null && !skipReason.toString().isBlank()) {
                    sb.append("- skipReason: ").append(s(skipReason)).append("\n\n");
                    continue;
                }

                sb.append("- queryCount: ").append(n(row.get("queryCount")))
                        .append(", queryTimeMs: ").append(n(row.get("queryTimeMs")))
                        .append(", avgMs: ").append(s(row.get("queryAvgTimeMs"))).append('\n');
                sb.append("- sqlCount: ").append(n(row.get("sqlCount")))
                        .append(", uniqueSqlCount: ").append(n(row.get("uniqueSqlCount")))
                        .append(", repeatRate: ").append(s(row.get("repeatRate")))
                        .append(", maxRepeat: ").append(n(row.get("maxRepeat")))
                        .append(", n+1: ").append(flag(row.get("nPlusOneSuspect"))).append('\n');
                String hints = joinHints(row.get("hints"));
                if (!hints.isBlank()) {
                    sb.append("- hints: ").append(hints).append('\n');
                }
                sb.append('\n');

                Object queriesObj = row.get("queries");
                if (!(queriesObj instanceof List<?> queries)) {
                    sb.append("(no sql)\n\n");
                    continue;
                }

                int idx = 1;
                for (Object qObj : queries) {
                    if (!(qObj instanceof Map<?, ?> q)) continue;
                    sb.append("##### Q").append(idx++).append(" (count ").append(n(q.get("count"))).append(")\n\n");
                    sb.append("```sql\n");
                    sb.append(prettySql(s(q.get("sampleSql")))).append('\n');
                    sb.append("```\n\n");
                }
            }

            sb.append("</details>\n\n");
        }

        return sb.toString();
    }

    private MockHttpServletRequestBuilder buildRequest(QueryScenario scenario) throws Exception {
        if (scenario.multipart() != null) {
            MultipartSpec spec = scenario.multipart();
            MockMultipartFile file = new MockMultipartFile(
                    spec.name(),
                    spec.filename(),
                    spec.contentType(),
                    spec.content()
            );
            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(scenario.path())
                    .file(file);
            applyParams(builder, scenario.params());
            return builder;
        }

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(
                HttpMethod.valueOf(scenario.method()),
                scenario.path()
        );
        applyParams(builder, scenario.params());
        if (scenario.body() != null) {
            builder.contentType(MediaType.APPLICATION_JSON);
            builder.content(objectMapper.writeValueAsString(scenario.body()));
        }
        return builder;
    }

    private void applyParams(MockHttpServletRequestBuilder builder, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.param(entry.getKey(), entry.getValue());
        }
    }

    private String displayPath(QueryScenario scenario) {
        if (scenario.params() == null || scenario.params().isEmpty()) {
            return scenario.path();
        }
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : scenario.params().entrySet()) {
            pairs.add(entry.getKey() + "=" + entry.getValue());
        }
        return scenario.path() + "?" + String.join("&", pairs);
    }

    private Map<String, Object> skippedRow(QueryScenario scenario) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("domain", scenario.domain());
        row.put("name", scenario.name());
        row.put("method", scenario.method());
        row.put("path", displayPath(scenario));
        row.put("status", "SKIPPED");
        row.put("queryCount", null);
        row.put("queryTimeMs", null);
        row.put("queryAvgTimeMs", null);
        row.put("sqlCount", 0);
        row.put("uniqueSqlCount", 0);
        row.put("repeatRate", "0.00");
        row.put("maxRepeat", 0);
        row.put("nPlusOneSuspect", false);
        row.put("hints", List.of());
        row.put("queries", List.of());
        row.put("skipReason", scenario.skipReason());
        return row;
    }

    private List<Map<String, Object>> aggregateQueries(List<String> sqlList) {
        if (sqlList == null || sqlList.isEmpty()) return List.of();

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String sql : sqlList) {
            counts.merge(sql, 1, Integer::sum);
        }

        return counts.entrySet().stream()
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("count", e.getValue());
                    row.put("sampleSql", e.getKey());
                    return row;
                })
                .sorted(Comparator.<Map<String, Object>, Integer>comparing(m -> ((Number) m.get("count")).intValue())
                        .reversed())
                .toList();
    }

    private List<Map<String, Object>> normalizeRows(List<?> rows) {
        List<Map<String, Object>> normalized = new ArrayList<>();
        for (Object rowObj : rows) {
            if (!(rowObj instanceof Map<?, ?> row)) continue;
            normalized.add(toStringKeyMap(row));
        }
        return normalized;
    }

    private Map<String, Object> toStringKeyMap(Map<?, ?> row) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : row.entrySet()) {
            Object key = entry.getKey();
            if (key == null) continue;
            out.put(key.toString(), entry.getValue());
        }
        return out;
    }

    private Map<String, List<Map<String, Object>>> groupByDomain(List<Map<String, Object>> rows) {
        Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object domainValue = row.get("domain");
            String domain = domainValue != null ? domainValue.toString() : "unknown";
            grouped.computeIfAbsent(domain, key -> new ArrayList<>())
                    .add(row);
        }
        return grouped;
    }

    private int maxRepeat(List<Map<String, Object>> queries) {
        if (queries == null || queries.isEmpty()) {
            return 0;
        }
        Object count = queries.getFirst().get("count");
        if (count instanceof Number n) {
            return n.intValue();
        }
        return 0;
    }

    private List<String> buildHints(List<String> sqlList) {
        if (sqlList == null || sqlList.isEmpty()) {
            return List.of();
        }
        boolean hasUserByLoginId = hasPattern(sqlList, "from users", "login_id=?");
        boolean hasUserById = hasUserIdLookup(sqlList);
        boolean hasCompanyById = hasPattern(sqlList, "from companies", "id=?");

        List<String> hints = new ArrayList<>();
        if (hasUserByLoginId) {
            hints.add("AUTH_USER_LOOKUP");
        }
        if (hasUserByLoginId && hasUserById) {
            hints.add("USER_RELOAD");
        }
        if (hasCompanyById) {
            hints.add("LAZY_COMPANY_LOAD");
        }
        return hints;
    }

    private boolean hasUserIdLookup(List<String> sqlList) {
        for (String sql : sqlList) {
            if (sql == null) continue;
            String lower = sql.toLowerCase(Locale.ROOT);
            if (!lower.contains("from users")) continue;
            if (!lower.contains("id=?")) continue;
            if (lower.contains("login_id=?")) continue;
            return true;
        }
        return false;
    }

    private boolean hasPattern(List<String> sqlList, String... parts) {
        for (String sql : sqlList) {
            if (sql == null) continue;
            String lower = sql.toLowerCase(Locale.ROOT);
            boolean matched = true;
            for (String part : parts) {
                if (part == null || part.isBlank()) continue;
                if (!lower.contains(part.toLowerCase(Locale.ROOT))) {
                    matched = false;
                    break;
                }
            }
            if (matched) return true;
        }
        return false;
    }

    private String joinHints(Object hintsObj) {
        if (hintsObj instanceof List<?> list) {
            List<String> parts = new ArrayList<>();
            for (Object v : list) {
                if (v == null) continue;
                String s = v.toString();
                if (!s.isBlank()) parts.add(s);
            }
            return String.join(", ", parts);
        }
        return "";
    }

    private String prettySql(String sql) {
        if (sql == null) return "";
        String normalized = sql.replaceAll("\\s+", " ").trim();
        if (normalized.isBlank()) return "";

        String pretty = normalized;
        pretty = pretty.replaceAll("(?i)\\s+left\\s+join\\s+", "\nLEFT JOIN ");
        pretty = pretty.replaceAll("(?i)\\s+inner\\s+join\\s+", "\nINNER JOIN ");
        pretty = pretty.replaceAll("(?i)\\s+right\\s+join\\s+", "\nRIGHT JOIN ");
        pretty = pretty.replaceAll("(?i)\\s+full\\s+join\\s+", "\nFULL JOIN ");
        pretty = pretty.replaceAll("(?i)\\s+join\\s+", "\nJOIN ");
        pretty = pretty.replaceAll("(?i)\\s+from\\s+", "\nFROM ");
        pretty = pretty.replaceAll("(?i)\\s+where\\s+", "\nWHERE ");
        pretty = pretty.replaceAll("(?i)\\s+group\\s+by\\s+", "\nGROUP BY ");
        pretty = pretty.replaceAll("(?i)\\s+having\\s+", "\nHAVING ");
        pretty = pretty.replaceAll("(?i)\\s+order\\s+by\\s+", "\nORDER BY ");
        pretty = pretty.replaceAll("(?i)\\s+limit\\s+", "\nLIMIT ");
        pretty = pretty.replaceAll("(?i)\\s+offset\\s+", "\nOFFSET ");
        return pretty;
    }

    private String s(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private String n(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private String flag(Object v) {
        if (v instanceof Boolean b) {
            return b ? "Y" : "";
        }
        return "";
    }
}
