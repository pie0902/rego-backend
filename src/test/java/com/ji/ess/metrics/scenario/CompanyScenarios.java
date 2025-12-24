package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioAuth;
import com.ji.ess.metrics.ScenarioFixture;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompanyScenarios implements QueryScenarioProvider {
    @Override
    public String domain() {
        return "company";
    }

    @Override
    public List<QueryScenario> scenarios(ScenarioFixture fixture) {
        Map<String, Object> createBody = new LinkedHashMap<>();
        createBody.put("companyName", "Portfolio Company");
        createBody.put("businessNumber", "555-66-77777");
        createBody.put("address", "seoul");
        createBody.put("ceoName", "Portfolio CEO");
        createBody.put("email", "portfolio.company@test.com");

        Map<String, String> searchParams = new LinkedHashMap<>();
        searchParams.put("name", "Portfolio");

        QueryScenario deleteAll = QueryScenario.delete(domain(), "company_delete_all", "/api/companies/deleteAll", ScenarioAuth.CEO)
                .skip("destructive endpoint skipped in tests");
        QueryScenario truncate = QueryScenario.delete(domain(), "company_truncate", "/api/companies/truncate", ScenarioAuth.CEO)
                .skip("destructive endpoint skipped in tests");

        return List.of(
                QueryScenario.postJson(domain(), "company_create", "/api/companies/create", ScenarioAuth.CEO, createBody),
                QueryScenario.get(domain(), "company_all", "/api/companies/all", ScenarioAuth.CEO),
                QueryScenario.get(domain(), "company_search", "/api/companies/search", ScenarioAuth.CEO)
                        .withParams(searchParams),
                deleteAll,
                truncate
        );
    }
}
