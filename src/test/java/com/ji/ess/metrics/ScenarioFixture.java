package com.ji.ess.metrics;

public record ScenarioFixture(
        Long companyId,
        Long ruleCompanyId,
        Long ceoId,
        Long employeeId,
        Long pendingEmployeeId,
        Long balanceTargetUserId,
        Long leaveRequestId,
        int leaveYear,
        String ceoLoginId,
        String ceoPassword,
        String employeeLoginId,
        String employeePassword
) {
}
