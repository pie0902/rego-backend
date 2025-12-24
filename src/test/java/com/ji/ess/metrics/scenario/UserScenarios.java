package com.ji.ess.metrics.scenario;

import com.ji.ess.metrics.MultipartSpec;
import com.ji.ess.metrics.QueryScenario;
import com.ji.ess.metrics.ScenarioAuth;
import com.ji.ess.metrics.ScenarioFixture;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserScenarios implements QueryScenarioProvider {
    @Override
    public String domain() {
        return "user";
    }

    @Override
    public List<QueryScenario> scenarios(ScenarioFixture fixture) {
        Map<String, Object> ceoUser = new LinkedHashMap<>();
        ceoUser.put("loginId", "portfolio_ceo");
        ceoUser.put("username", "Portfolio CEO");
        ceoUser.put("password", "password123!");
        ceoUser.put("email", "portfolio.ceo@test.com");
        ceoUser.put("phone", "010-1111-2222");
        ceoUser.put("address", "seoul");
        ceoUser.put("hireDate", LocalDate.now().minusYears(1).toString());

        Map<String, Object> ceoCompany = new LinkedHashMap<>();
        ceoCompany.put("companyName", "Portfolio Co");
        ceoCompany.put("businessNumber", "999-88-77777");
        ceoCompany.put("address", "seoul");
        ceoCompany.put("ceoName", "Portfolio CEO");
        ceoCompany.put("email", "portfolio.hr@test.com");

        Map<String, Object> ceoBody = new LinkedHashMap<>();
        ceoBody.put("user", ceoUser);
        ceoBody.put("company", ceoCompany);

        Map<String, Object> employeeBody = new LinkedHashMap<>();
        employeeBody.put("loginId", "portfolio_emp");
        employeeBody.put("username", "Portfolio Emp");
        employeeBody.put("password", "password123!");
        employeeBody.put("email", "portfolio.emp@test.com");
        employeeBody.put("phone", "010-3333-4444");
        employeeBody.put("address", "seoul");
        employeeBody.put("hireDate", LocalDate.now().minusMonths(3).toString());
        employeeBody.put("companyId", fixture.companyId());

        Map<String, Object> approveBody = new LinkedHashMap<>();
        approveBody.put("department", "dev");
        approveBody.put("position", "staff");
        approveBody.put("role", "EMPLOYEE");

        Map<String, Object> updateBody = new LinkedHashMap<>();
        updateBody.put("username", "Updated Name");
        updateBody.put("email", "updated@test.com");
        updateBody.put("phone", "010-0000-0000");
        updateBody.put("department", "dev");
        updateBody.put("position", "associate");
        updateBody.put("address", "seoul");

        Map<String, Object> passwordBody = new LinkedHashMap<>();
        passwordBody.put("password", "newpass1234!");

        MultipartSpec profileImage = new MultipartSpec(
                "image",
                "profile.png",
                "image/png",
                new byte[]{1, 2, 3, 4}
        );

        return List.of(
                QueryScenario.postJson(domain(), "user_register_ceo", "/api/users/ceo", ScenarioAuth.ANONYMOUS, ceoBody),
                QueryScenario.postJson(domain(), "user_register_employee", "/api/users/employee", ScenarioAuth.ANONYMOUS, employeeBody),
                QueryScenario.get(domain(), "user_pending", "/api/users/pending", ScenarioAuth.CEO),
                QueryScenario.putJson(domain(), "user_approve", "/api/users/approve/" + fixture.pendingEmployeeId(), ScenarioAuth.CEO, approveBody),
                QueryScenario.get(domain(), "user_all", "/api/users/getAllUsers", ScenarioAuth.CEO),
                QueryScenario.get(domain(), "user_me", "/api/users/me", ScenarioAuth.EMPLOYEE),
                QueryScenario.putJson(domain(), "user_update_me", "/api/users/me", ScenarioAuth.EMPLOYEE, updateBody),
                QueryScenario.postJson(domain(), "user_update_password", "/api/users/updatePassword", ScenarioAuth.EMPLOYEE, passwordBody),
                QueryScenario.multipart(domain(), "user_profile_image", "/api/users/" + fixture.employeeId() + "/profile-image", ScenarioAuth.EMPLOYEE, profileImage)
        );
    }
}
