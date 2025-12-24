package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioAuth;
import com.ji.ess.metrics.ScenarioFixture;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AuthScenarios implements QueryScenarioProvider {
    @Override
    public String domain() {
        return "auth";
    }

    @Override
    public List<QueryScenario> scenarios(ScenarioFixture fixture) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("loginId", fixture.ceoLoginId());
        body.put("password", fixture.ceoPassword());

        return List.of(
                QueryScenario.postJson(domain(), "auth_login", "/api/auth/login", ScenarioAuth.ANONYMOUS, body)
        );
    }
}
