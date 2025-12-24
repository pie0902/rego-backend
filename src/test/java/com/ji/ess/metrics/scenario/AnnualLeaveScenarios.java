package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioAuth;
import com.ji.ess.metrics.ScenarioFixture;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnualLeaveScenarios implements QueryScenarioProvider {
    @Override
    public String domain() {
        return "annual_leave";
    }

    @Override
    public List<QueryScenario> scenarios(ScenarioFixture fixture) {
        Map<String, Object> createBody = new LinkedHashMap<>();
        createBody.put("userId", fixture.balanceTargetUserId());
        createBody.put("year", fixture.leaveYear());
        createBody.put("granted", 10);

        Map<String, Object> useBody = new LinkedHashMap<>();
        useBody.put("year", fixture.leaveYear());
        useBody.put("days", 1);

        Map<String, Object> rollbackBody = new LinkedHashMap<>();
        rollbackBody.put("year", fixture.leaveYear());
        rollbackBody.put("days", 1);

        Map<String, String> statutoryMeParams = new LinkedHashMap<>();
        statutoryMeParams.put("asOf", LocalDate.now().toString());

        Map<String, String> statutoryParams = new LinkedHashMap<>();
        statutoryParams.put("userId", String.valueOf(fixture.employeeId()));
        statutoryParams.put("asOf", LocalDate.now().toString());

        return List.of(
                QueryScenario.postJson(domain(), "annual_leave_create", "/api/annual-leave/create", ScenarioAuth.CEO, createBody),
                QueryScenario.get(domain(), "annual_leave_me", "/api/annual-leave/me", ScenarioAuth.EMPLOYEE),
                QueryScenario.postJson(domain(), "annual_leave_use", "/api/annual-leave/use", ScenarioAuth.CEO, useBody),
                QueryScenario.postJson(domain(), "annual_leave_rollback", "/api/annual-leave/rollback", ScenarioAuth.CEO, rollbackBody),
                QueryScenario.get(domain(), "annual_leave_statutory_me", "/api/annual-leave/statutory/me", ScenarioAuth.EMPLOYEE)
                        .withParams(statutoryMeParams),
                QueryScenario.get(domain(), "annual_leave_statutory_admin", "/api/annual-leave/statutory", ScenarioAuth.CEO)
                        .withParams(statutoryParams)
        );
    }
}
