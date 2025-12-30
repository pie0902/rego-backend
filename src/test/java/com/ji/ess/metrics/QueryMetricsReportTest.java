package com.ji.ess.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ji.ess.annual_leave_balance.entity.AnnualLeaveBalance;
import com.ji.ess.annual_leave_balance.repository.AnnualLeaveBalanceRepository;
import com.ji.ess.attendance.repository.AttendanceRepository;
import com.ji.ess.company.entity.Company;
import com.ji.ess.company.repository.CompanyRepository;
import com.ji.ess.companyRule.entity.CompanyRule;
import com.ji.ess.companyRule.entity.WorkType;
import com.ji.ess.companyRule.repository.CompanyRuleRepository;
import com.ji.ess.global.jwt.JwtTokenProvider;
import com.ji.ess.leave_request.entity.LeaveRequest;
import com.ji.ess.leave_request.entity.LeaveType;
import com.ji.ess.leave_request.repository.LeaveRequestRepository;
import com.ji.ess.metrics.scenario.AnnualLeaveScenarios;
import com.ji.ess.metrics.scenario.AttendanceScenarios;
import com.ji.ess.metrics.scenario.AuthScenarios;
import com.ji.ess.metrics.scenario.CompanyRuleScenarios;
import com.ji.ess.metrics.scenario.CompanyScenarios;
import com.ji.ess.metrics.scenario.DashboardScenarios;
import com.ji.ess.metrics.scenario.LeaveRequestScenarios;
import com.ji.ess.metrics.scenario.QueryScenarioProvider;
import com.ji.ess.metrics.scenario.UserScenarios;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.entity.UserRole;
import com.ji.ess.user.entity.UserStatus;
import com.ji.ess.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SqlCaptureTestConfig.class)
class QueryMetricsReportTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyRuleRepository companyRuleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private AnnualLeaveBalanceRepository annualLeaveBalanceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SqlCapture sqlCapture;

    private QueryMetricsTestSupport support;
    private ScenarioFixture fixture;
    private ScenarioTokens tokens;

    @BeforeEach
    void setUp() {
        leaveRequestRepository.deleteAll();
        attendanceRepository.deleteAll();
        annualLeaveBalanceRepository.deleteAll();
        companyRuleRepository.deleteAll();
        userRepository.deleteAll();
        companyRepository.deleteAll();

        int leaveYear = LocalDate.now().getYear();

        Company company = Company.builder()
                .companyName("Test Company")
                .businessNumber("123-45-67890")
                .address("seoul")
                .ceoName("대표")
                .email("hr@test.com")
                .build();
        companyRepository.save(company);

        Company ruleCompany = Company.builder()
                .companyName("Rule Company")
                .businessNumber("234-56-78901")
                .address("seoul")
                .ceoName("Rule CEO")
                .email("rule@test.com")
                .build();
        companyRepository.save(ruleCompany);

        String ceoLoginId = "ceo1";
        String ceoPassword = "password123!";
        User ceo = User.builder()
                .loginId(ceoLoginId)
                .username("대표")
                .password(passwordEncoder.encode(ceoPassword))
                .email("ceo1@test.com")
                .phone("010-0000-0000")
                .address("seoul")
                .role(UserRole.CEO)
                .active(UserStatus.ACTIVE)
                .hireDate(LocalDate.now().minusYears(2))
                .build();
        ceo.assignCompany(company);
        userRepository.save(ceo);
        String ceoToken = jwtTokenProvider.generateToken(ceo.getLoginId(), ceo.getRole());

        String employeeLoginId = "employee1";
        String employeePassword = "password123!";
        User employee = User.builder()
                .loginId(employeeLoginId)
                .username("사원")
                .password(passwordEncoder.encode(employeePassword))
                .email("employee1@test.com")
                .phone("010-1111-1111")
                .address("seoul")
                .role(UserRole.EMPLOYEE)
                .active(UserStatus.ACTIVE)
                .hireDate(LocalDate.now().minusYears(1))
                .build();
        employee.assignCompany(company);
        userRepository.save(employee);
        String employeeToken = jwtTokenProvider.generateToken(employee.getLoginId(), employee.getRole());

        User pendingEmployee = User.builder()
                .loginId("pending1")
                .username("대기")
                .password("ignored")
                .email("pending1@test.com")
                .phone("010-2222-2222")
                .address("seoul")
                .role(UserRole.EMPLOYEE)
                .active(UserStatus.PENDING)
                .hireDate(LocalDate.now())
                .build();
        pendingEmployee.assignCompany(company);
        userRepository.save(pendingEmployee);

        User balanceTarget = User.builder()
                .loginId("balance1")
                .username("Balance User")
                .password("ignored")
                .email("balance1@test.com")
                .phone("010-3333-3333")
                .address("seoul")
                .role(UserRole.EMPLOYEE)
                .active(UserStatus.ACTIVE)
                .hireDate(LocalDate.now().minusYears(1))
                .build();
        balanceTarget.assignCompany(company);
        userRepository.save(balanceTarget);

        CompanyRule rule = CompanyRule.builder()
                .company(company)
                .workType(WorkType.FIXED)
                .standardCheckIn(LocalTime.of(9, 0))
                .standardCheckOut(LocalTime.of(18, 0))
                .requiredHours(BigDecimal.valueOf(8))
                .lateTolerance(10)
                .earlyLeaveTolerance(10)
                .weekendWorkAllowed(false)
                .build();
        companyRuleRepository.save(rule);

        annualLeaveBalanceRepository.save(AnnualLeaveBalance.builder()
                .user(employee)
                .year(leaveYear)
                .granted(BigDecimal.valueOf(10))
                .build());
        annualLeaveBalanceRepository.save(AnnualLeaveBalance.builder()
                .user(ceo)
                .year(leaveYear)
                .granted(BigDecimal.valueOf(5))
                .build());

        LeaveRequest pendingRequest = LeaveRequest.builder()
                .user(employee)
                .company(company)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .type(LeaveType.FULL_DAY)
                .reason("test")
                .requestedAt(LocalDateTime.now())
                .build();
        leaveRequestRepository.save(pendingRequest);

        for (int i = 0; i < 10; i++) {
            User extra = User.builder()
                    .loginId("emp" + i)
                    .username("Emp" + i)
                    .password("ignored")
                    .email("emp" + i + "@test.com")
                    .phone("010-4444-44" + String.format("%02d", i))
                    .address("seoul")
                    .role(UserRole.EMPLOYEE)
                    .active(UserStatus.ACTIVE)
                    .hireDate(LocalDate.now())
                    .build();
            extra.assignCompany(company);
            userRepository.save(extra);

            LeaveRequest request = LeaveRequest.builder()
                    .user(extra)
                    .company(company)
                    .startDate(LocalDate.now().plusDays(1))
                    .endDate(LocalDate.now().plusDays(1))
                    .type(LeaveType.FULL_DAY)
                    .reason("test")
                    .requestedAt(LocalDateTime.now())
                    .build();
            leaveRequestRepository.save(request);
        }

        support = new QueryMetricsTestSupport(mockMvc, objectMapper, sqlCapture);
        tokens = new ScenarioTokens(ceoToken, employeeToken);
        fixture = new ScenarioFixture(
                company.getId(),
                ruleCompany.getId(),
                ceo.getId(),
                employee.getId(),
                pendingEmployee.getId(),
                balanceTarget.getId(),
                pendingRequest.getId(),
                leaveYear,
                ceoLoginId,
                ceoPassword,
                employeeLoginId,
                employeePassword
        );
    }

    @Test
    void generates_query_metrics_report() throws Exception {
        List<QueryScenarioProvider> providers = List.of(
                new AuthScenarios(),
                new CompanyRuleScenarios(),
                new AttendanceScenarios(),
                new DashboardScenarios(),
                new AnnualLeaveScenarios(),
                new LeaveRequestScenarios(),
                new UserScenarios(),
                new CompanyScenarios()
        );

        List<Map<String, Object>> rows = new ArrayList<>();
        for (QueryScenarioProvider provider : providers) {
            for (QueryScenario scenario : provider.scenarios(fixture)) {
                rows.add(support.call(scenario, tokens));
            }
        }

        String runId = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT)
                .format(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
        Path outDir = Path.of("build", "reports", "query-metrics", runId);
        Files.createDirectories(outDir);
        Path jsonFile = outDir.resolve("report.json");
        Path mdFile = outDir.resolve("report.md");

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("generatedAt", ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toString());
        report.put("rows", rows);

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile.toFile(), report);
        Files.writeString(mdFile, support.toMarkdown(report));

        Path latestDir = Path.of("build", "reports", "query-metrics", "latest");
        Files.createDirectories(latestDir);
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(latestDir.resolve("report.json").toFile(), report);
        Files.writeString(latestDir.resolve("report.md"), support.toMarkdown(report));

        assertThat(Files.exists(jsonFile)).isTrue();
        assertThat(Files.exists(mdFile)).isTrue();
    }

    @Test
    void attendance_me_query_budget() throws Exception {
        Map<String, Object> row = support.call(AttendanceScenarios.attendanceMe(), tokens);
        assertThat(((Number) row.get("queryCount")).longValue()).isLessThanOrEqualTo(2L);
    }

    @Test
    void leave_request_pending_query_budget() throws Exception {
        Map<String, Object> row = support.call(QueryScenario.get("leave_request", "leave_request_pending", "/api/leaveRequest/pending", ScenarioAuth.CEO), tokens);
        assertThat(((Number) row.get("queryCount")).longValue()).isLessThanOrEqualTo(4L);
    }
}
