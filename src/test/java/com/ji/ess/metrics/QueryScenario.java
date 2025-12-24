package com.ji.ess.metrics;

import java.util.Map;

public record QueryScenario(
        String domain,
        String name,
        String method,
        String path,
        ScenarioAuth auth,
        Object body,
        Map<String, String> params,
        MultipartSpec multipart,
        boolean skip,
        String skipReason
) {
    public QueryScenario {
        if (params == null) {
            params = Map.of();
        }
    }

    public static QueryScenario get(String domain, String name, String path, ScenarioAuth auth) {
        return new QueryScenario(domain, name, "GET", path, auth, null, Map.of(), null, false, null);
    }

    public static QueryScenario post(String domain, String name, String path, ScenarioAuth auth) {
        return new QueryScenario(domain, name, "POST", path, auth, null, Map.of(), null, false, null);
    }

    public static QueryScenario postJson(String domain, String name, String path, ScenarioAuth auth, Object body) {
        return new QueryScenario(domain, name, "POST", path, auth, body, Map.of(), null, false, null);
    }

    public static QueryScenario putJson(String domain, String name, String path, ScenarioAuth auth, Object body) {
        return new QueryScenario(domain, name, "PUT", path, auth, body, Map.of(), null, false, null);
    }

    public static QueryScenario patchJson(String domain, String name, String path, ScenarioAuth auth, Object body) {
        return new QueryScenario(domain, name, "PATCH", path, auth, body, Map.of(), null, false, null);
    }

    public static QueryScenario delete(String domain, String name, String path, ScenarioAuth auth) {
        return new QueryScenario(domain, name, "DELETE", path, auth, null, Map.of(), null, false, null);
    }

    public static QueryScenario multipart(String domain, String name, String path, ScenarioAuth auth, MultipartSpec multipart) {
        return new QueryScenario(domain, name, "POST", path, auth, null, Map.of(), multipart, false, null);
    }

    public QueryScenario withParams(Map<String, String> params) {
        return new QueryScenario(domain, name, method, path, auth, body, params, multipart, skip, skipReason);
    }

    public QueryScenario skip(String reason) {
        return new QueryScenario(domain, name, method, path, auth, body, params, multipart, true, reason);
    }
}
