package com.ji.ess.annual_leave_balance.service;

import com.ji.ess.annual_leave_balance.entity.AnnualLeaveBalance;
import com.ji.ess.annual_leave_balance.repository.AnnualLeaveBalanceRepository;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.math.BigDecimal;

@Component
@ConditionalOnProperty(name = "feature.firstYearMonthlyGrant.enabled", havingValue = "true", matchIfMissing = false)
public class FirstYearMonthlyGrantJob {

    private final UserRepository userRepository;
    private final AnnualLeaveBalanceRepository annualLeaveBalanceRepository;

    public FirstYearMonthlyGrantJob(UserRepository userRepository,
                                    AnnualLeaveBalanceRepository annualLeaveBalanceRepository) {
        this.userRepository = userRepository;
        this.annualLeaveBalanceRepository = annualLeaveBalanceRepository;
    }

    // 매일 00:10에 실행
    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void runDailyMonthlyGrant() {
        LocalDate today = LocalDate.now();

        // 입사 1년 미만 후보 조회(범위 필터 후 최종 years < 1로 재확인)
        LocalDate rangeStart = today.minusYears(1); // exclusive로 다루고 코드에서 재확인
        List<User> candidates = userRepository.findByHireDateBetween(rangeStart, today);

        for (User user : candidates) {
            LocalDate hireDate = user.getHireDate();
            if (hireDate == null) continue;
            int years = Period.between(hireDate, today).getYears();
            if (years >= 1) continue; // 1년 미만만 대상

            int expectedThisYear = countMonthlyAccrualsInYear(hireDate, today);
            int year = today.getYear();

            AnnualLeaveBalance balance = annualLeaveBalanceRepository
                    .findByUserIdAndYear(user.getId(), year)
                    .orElseGet(() -> {
                        // 해당 연도 레코드 없으면 생성(초기 granted=0)
                        AnnualLeaveBalance created = AnnualLeaveBalance.builder()
                                .user(user)
                                .year(year)
                                .granted(BigDecimal.ZERO)
                                .build();
                        return annualLeaveBalanceRepository.save(created);
                    });

            BigDecimal delta = BigDecimal.valueOf(expectedThisYear).subtract(balance.getGranted());
            if (delta.compareTo(BigDecimal.ZERO) > 0) {
                balance.grantExtraDays(delta);
            }
        }
    }

    private int countMonthlyAccrualsInYear(LocalDate hireDate, LocalDate asOf) {
        int count = 0;
        LocalDate firstAnniversary = hireDate.plusYears(1);
        for (int i = 1; i <= 11; i++) {
            LocalDate monthAnniversary = hireDate.plusMonths(i);
            if (!monthAnniversary.isBefore(firstAnniversary)
                    || monthAnniversary.isAfter(asOf)) {
                break;
            }
            if (monthAnniversary.getYear() == asOf.getYear()) {
                count++;
            }
        }
        return count;
    }
}
