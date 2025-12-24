package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioAuth;
import com.ji.ess.metrics.ScenarioFixture;

import java.util.List;

public class DashboardScenarios implements QueryScenarioProvider {
    @Override
    public String domain() {
        return "dashboard";
    }

    @Override
    public List<QueryScenario> scenarios(ScenarioFixture fixture) {
        return List.of(
                QueryScenario.get(domain(), "dashboard_me", "/api/dashboard/me", ScenarioAuth.EMPLOYEE)
        );
    }
}
