package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioFixture;

import java.util.List;

public interface QueryScenarioProvider {
    String domain();

    List<QueryScenario> scenarios(ScenarioFixture fixture);
}
