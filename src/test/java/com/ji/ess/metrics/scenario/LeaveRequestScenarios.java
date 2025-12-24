package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioAuth;
import com.ji.ess.metrics.ScenarioFixture;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeaveRequestScenarios implements QueryScenarioProvider {
    @Override
    public String domain() {
        return "leave_request";
    }

    @Override
    public List<QueryScenario> scenarios(ScenarioFixture fixture) {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start;

        Map<String, Object> createBody = new LinkedHashMap<>();
        createBody.put("startDate", start.toString());
        createBody.put("endDate", end.toString());
        createBody.put("type", "FULL_DAY");
        createBody.put("reason", "test");

        Map<String, Object> updateBody = new LinkedHashMap<>();
        updateBody.put("leaveRequestId", fixture.leaveRequestId());
        updateBody.put("status", "APPROVED");

        return List.of(
                QueryScenario.postJson(domain(), "leave_request_create", "/api/leaveRequest/create", ScenarioAuth.EMPLOYEE, createBody),
                QueryScenario.get(domain(), "leave_request_pending", "/api/leaveRequest/pending", ScenarioAuth.CEO),
                QueryScenario.get(domain(), "leave_request_my", "/api/leaveRequest/my", ScenarioAuth.EMPLOYEE),
                QueryScenario.patchJson(domain(), "leave_request_update_status", "/api/leaveRequest/update-status", ScenarioAuth.CEO, updateBody)
        );
    }
}
