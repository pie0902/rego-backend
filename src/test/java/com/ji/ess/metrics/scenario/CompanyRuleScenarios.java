package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioAuth;
import com.ji.ess.metrics.ScenarioFixture;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompanyRuleScenarios implements QueryScenarioProvider {
    @Override
    public String domain() {
        return "company_rule";
    }

    @Override
    public List<QueryScenario> scenarios(ScenarioFixture fixture) {
        Map<String, Object> createBody = new LinkedHashMap<>();
        createBody.put("companyId", fixture.ruleCompanyId());
        createBody.put("workType", "FIXED");
        createBody.put("standardCheckIn", "09:00:00");
        createBody.put("standardCheckOut", "18:00:00");
        createBody.put("requiredHours", 8);
        createBody.put("lateTolerance", 10);
        createBody.put("earlyLeaveTolerance", 10);
        createBody.put("weekendWorkAllowed", false);

        Map<String, Object> updateBody = new LinkedHashMap<>();
        updateBody.put("companyId", fixture.ruleCompanyId());
        updateBody.put("workType", "FLEXIBLE");
        updateBody.put("standardCheckIn", "10:00:00");
        updateBody.put("standardCheckOut", "19:00:00");
        updateBody.put("requiredHours", 7.5);
        updateBody.put("lateTolerance", 5);
        updateBody.put("earlyLeaveTolerance", 5);
        updateBody.put("weekendWorkAllowed", true);

        Map<String, String> getParams = new LinkedHashMap<>();
        getParams.put("companyId", String.valueOf(fixture.ruleCompanyId()));

        return List.of(
                QueryScenario.postJson(domain(), "company_rule_create", "/api/CompanyRule/createRule", ScenarioAuth.CEO, createBody),
                QueryScenario.get(domain(), "company_rule_get", "/api/CompanyRule/get", ScenarioAuth.CEO).withParams(getParams),
                QueryScenario.putJson(domain(), "company_rule_update", "/api/CompanyRule/update", ScenarioAuth.CEO, updateBody)
        );
    }
}
