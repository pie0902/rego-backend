package com.ji.ess.global.bootstrap;

import com.ji.ess.annual_leave_balance.entity.AnnualLeaveBalance;
import com.ji.ess.annual_leave_balance.repository.AnnualLeaveBalanceRepository;
import com.ji.ess.attendance.entity.Attendance;
import com.ji.ess.attendance.repository.AttendanceRepository;
import com.ji.ess.company.entity.Company;
import com.ji.ess.company.repository.CompanyRepository;
import com.ji.ess.companyRule.entity.CompanyRule;
import com.ji.ess.companyRule.entity.WorkType;
import com.ji.ess.companyRule.repository.CompanyRuleRepository;
import com.ji.ess.leave_request.entity.LeaveRequest;
import com.ji.ess.leave_request.entity.LeaveType;
import com.ji.ess.leave_request.repository.LeaveRequestRepository;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.entity.UserRole;
import com.ji.ess.user.entity.UserStatus;
import com.ji.ess.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Profile("!test")
@ConditionalOnProperty(name = "feature.dummy-data.enabled", havingValue = "true", matchIfMissing = false)
public class DummyDataInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DummyDataInitializer.class);

    private static final String DUMMY_FILE = "dummy.md";

    private final CompanyRepository companyRepository;
    private final CompanyRuleRepository companyRuleRepository;
    private final UserRepository userRepository;
    private final AnnualLeaveBalanceRepository annualLeaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    public DummyDataInitializer(CompanyRepository companyRepository,
                                CompanyRuleRepository companyRuleRepository,
                                UserRepository userRepository,
                                AnnualLeaveBalanceRepository annualLeaveBalanceRepository,
                                LeaveRequestRepository leaveRequestRepository,
                                AttendanceRepository attendanceRepository,
                                PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.companyRuleRepository = companyRuleRepository;
        this.userRepository = userRepository;
        this.annualLeaveBalanceRepository = annualLeaveBalanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Optional<DummyCredentials> credsOpt = DummyCredentials.load(DUMMY_FILE);
        if (credsOpt.isEmpty()) {
            log.warn("더미 데이터 초기화 스킵: {} 파일을 찾을 수 없습니다.", DUMMY_FILE);
            return;
        }
        DummyCredentials creds = credsOpt.get();

        User ceo = userRepository.findByLoginId(creds.ceoLoginId())
                .orElse(null);

        Company company;
        if (ceo == null) {
            company = companyRepository.save(Company.builder()
                    .companyName("더미회사")
                    .businessNumber("000-00-00000")
                    .address("Seoul")
                    .ceoName("더미대표")
                    .email("dummy-hr@example.com")
                    .build());

            ceo = User.builder()
                    .loginId(creds.ceoLoginId())
                    .username("더미대표")
                    .password(passwordEncoder.encode(creds.ceoPassword()))
                    .email("dummy-ceo@example.com")
                    .phone("010-0000-0000")
                    .address("Seoul")
                    .role(UserRole.CEO)
                    .active(UserStatus.ACTIVE)
                    .hireDate(LocalDate.now().minusYears(2))
                    .build();
            ceo.assignCompany(company);
            userRepository.save(ceo);
        } else {
            company = ceo.getCompany();
            if (company == null) {
                company = companyRepository.save(Company.builder()
                        .companyName("더미회사")
                        .businessNumber("000-00-00000")
                        .address("Seoul")
                        .ceoName("더미대표")
                        .email("dummy-hr@example.com")
                        .build());
                ceo.assignCompany(company);
                userRepository.save(ceo);
            }
        }

        User employee = userRepository.findByLoginId(creds.employeeLoginId())
                .orElse(null);
        if (employee == null) {
            User created = User.builder()
                    .loginId(creds.employeeLoginId())
                    .username("더미사원")
                    .password(passwordEncoder.encode(creds.employeePassword()))
                    .email("dummy-employee@example.com")
                    .phone("010-1111-1111")
                    .address("Seoul")
                    .role(UserRole.EMPLOYEE)
                    .active(UserStatus.ACTIVE)
                    .hireDate(LocalDate.now().minusMonths(6))
                    .build();
            created.assignCompany(company);
            employee = userRepository.save(created);
        }

        if (employee.getCompany() == null) {
            employee.assignCompany(company);
            userRepository.save(employee);
        }

        CompanyRule rule = companyRuleRepository.findByCompanyId(company.getId())
                .orElse(null);
        if (rule == null) {
            rule = companyRuleRepository.save(CompanyRule.builder()
                    .company(company)
                    .workType(WorkType.FIXED)
                    .standardCheckIn(LocalTime.of(9, 0))
                    .standardCheckOut(LocalTime.of(18, 0))
                    .requiredHours(BigDecimal.valueOf(8))
                    .lateTolerance(10)
                    .earlyLeaveTolerance(10)
                    .weekendWorkAllowed(false)
                    .build());
        }

        int year = LocalDate.now().getYear();
        if (annualLeaveBalanceRepository.findByUserIdAndYear(employee.getId(), year).isEmpty()) {
            annualLeaveBalanceRepository.save(AnnualLeaveBalance.builder()
                    .user(employee)
                    .year(year)
                    .granted(BigDecimal.valueOf(15))
                    .build());
        }

        if (leaveRequestRepository.findByUserIdOrderByRequestedAtDesc(employee.getId()).isEmpty()) {
            LeaveRequest pending = LeaveRequest.builder()
                    .user(employee)
                    .company(company)
                    .startDate(LocalDate.now().plusDays(1))
                    .endDate(LocalDate.now().plusDays(1))
                    .type(LeaveType.FULL_DAY)
                    .reason("더미 데이터")
                    .requestedAt(LocalDateTime.now())
                    .build();
            leaveRequestRepository.save(pending);
        }

        seedAttendance(employee, company, rule);

        log.info("더미 데이터 초기화 완료: companyId={}, ceoLoginId={}, employeeLoginId={}",
                company.getId(), creds.ceoLoginId(), creds.employeeLoginId());
    }

    private void seedAttendance(User employee, Company company, CompanyRule rule) {
        int targetDays = 10; // 최근 10영업일(오늘 제외)
        int created = 0;
        LocalDate cursor = LocalDate.now().minusDays(1);

        while (created < targetDays) {
            if (isWeekend(cursor)) {
                cursor = cursor.minusDays(1);
                continue;
            }

            if (attendanceRepository.existsByUserIdAndWorkDate(employee.getId(), cursor)) {
                cursor = cursor.minusDays(1);
                continue;
            }

            LocalTime checkIn = LocalTime.of(9, 0);
            LocalTime checkOut = LocalTime.of(18, 5);

            if (created == 2) { // 1회 지각
                checkIn = LocalTime.of(9, 25);
                checkOut = LocalTime.of(18, 20);
            } else if (created == 5) { // 1회 조퇴
                checkIn = LocalTime.of(9, 2);
                checkOut = LocalTime.of(17, 30);
            }

            Attendance attendance = Attendance.seed(employee, company, rule, cursor, checkIn, checkOut);
            attendanceRepository.save(attendance);

            created++;
            cursor = cursor.minusDays(1);
        }
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private record DummyCredentials(String ceoLoginId,
                                    String ceoPassword,
                                    String employeeLoginId,
                                    String employeePassword) {
        private static Optional<DummyCredentials> load(String filename) {
            Path path = Path.of(System.getProperty("user.dir")).resolve(filename);
            if (!Files.exists(path)) {
                return Optional.empty();
            }
            try {
                Map<String, String> values = parseKeyValues(Files.readString(path, StandardCharsets.UTF_8));
                String ceoLoginId = values.get("CEO_LOGIN_ID");
                String ceoPassword = values.get("CEO_PASSWORD");
                String employeeLoginId = values.get("EMPLOYEE_LOGIN_ID");
                String employeePassword = values.get("EMPLOYEE_PASSWORD");
                if (isBlank(ceoLoginId) || isBlank(ceoPassword) || isBlank(employeeLoginId) || isBlank(employeePassword)) {
                    return Optional.empty();
                }
                return Optional.of(new DummyCredentials(ceoLoginId.trim(), ceoPassword.trim(), employeeLoginId.trim(), employeePassword.trim()));
            } catch (IOException e) {
                return Optional.empty();
            }
        }

        private static Map<String, String> parseKeyValues(String content) {
            Map<String, String> out = new HashMap<>();
            if (content == null || content.isBlank()) {
                return out;
            }
            String[] lines = content.split("\\R");
            for (String line : lines) {
                if (line == null) continue;
                String trimmed = line.trim();
                if (trimmed.isBlank() || trimmed.startsWith("#")) continue;
                if (trimmed.startsWith("```")) continue;
                int idx = trimmed.indexOf('=');
                if (idx <= 0) continue;
                String key = trimmed.substring(0, idx).trim();
                String value = trimmed.substring(idx + 1).trim();
                if (!key.isBlank() && !value.isBlank()) {
                    out.put(key, value);
                }
            }
            return out;
        }

        private static boolean isBlank(String v) {
            return v == null || v.isBlank();
        }
    }
}
