package com.ji.ess.metrics;

public record ScenarioTokens(String ceoToken, String employeeToken) {
    public String resolve(ScenarioAuth auth) {
        if (auth == null || auth == ScenarioAuth.ANONYMOUS) {
            return null;
        }
        return auth == ScenarioAuth.CEO ? ceoToken : employeeToken;
    }
}
