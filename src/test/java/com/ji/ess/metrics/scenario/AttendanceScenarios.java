package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioAuth;
import com.ji.ess.metrics.ScenarioFixture;

import java.util.List;

public class AttendanceScenarios implements QueryScenarioProvider {
    @Override
    public String domain() {
        return "attendance";
    }

    @Override
    public List<QueryScenario> scenarios(ScenarioFixture fixture) {
        return List.of(
                QueryScenario.post(domain(), "attendance_check_in", "/api/attendance/check-in", ScenarioAuth.EMPLOYEE),
                QueryScenario.post(domain(), "attendance_check_out", "/api/attendance/check-out", ScenarioAuth.EMPLOYEE),
                QueryScenario.get(domain(), "attendance_me", "/api/attendance/me", ScenarioAuth.EMPLOYEE)
        );
    }

    public static QueryScenario attendanceMe() {
        return QueryScenario.get("attendance", "attendance_me", "/api/attendance/me", ScenarioAuth.EMPLOYEE);
    }
}
