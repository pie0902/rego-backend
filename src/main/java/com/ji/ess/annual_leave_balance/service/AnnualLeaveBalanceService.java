package com.ji.ess.annual_leave_balance.service;

import com.ji.ess.auth.dto.AuthenticatedUserDto;
import com.ji.ess.annual_leave_balance.dto.AnnualLeaveBalanceRequestDto;
import com.ji.ess.annual_leave_balance.dto.AnnualLeaveBalanceResponseDto;
import com.ji.ess.annual_leave_balance.dto.AnnualLeaveUsageRequestDto;
import com.ji.ess.annual_leave_balance.dto.AnnualLeaveStatutoryPreviewDto;
import com.ji.ess.annual_leave_balance.entity.AnnualLeaveBalance;
import com.ji.ess.annual_leave_balance.repository.AnnualLeaveBalanceRepository;
 
import com.ji.ess.user.entity.User;
import com.ji.ess.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

// 연차 관리 도메인 서비스
@Service
public class AnnualLeaveBalanceService {

    private final AnnualLeaveBalanceRepository annualLeaveBalanceRepository;
    private final UserRepository userRepository;
    

    public AnnualLeaveBalanceService(AnnualLeaveBalanceRepository annualLeaveBalanceRepository,
                                     UserRepository userRepository) {
        this.annualLeaveBalanceRepository = annualLeaveBalanceRepository;
        this.userRepository = userRepository;
    }

    // 연차 부여 생성
    @Transactional
    public AnnualLeaveBalanceResponseDto createBalance(AnnualLeaveBalanceRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (dto.getGranted() == null || dto.getGranted().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("부여 일수는 0보다 커야 합니다.");
        }

        annualLeaveBalanceRepository.findByUserIdAndYear(dto.getUserId(), dto.getYear())
                .ifPresent(balance -> {
                    throw new IllegalArgumentException("이미 해당 연도의 연차 정보가 존재합니다.");
                });

        AnnualLeaveBalance annualLeaveBalance = AnnualLeaveBalance.builder()
                .user(user)
                .year(dto.getYear())
                .granted(dto.getGranted())
                .build();
        annualLeaveBalanceRepository.save(annualLeaveBalance);
        return new AnnualLeaveBalanceResponseDto(annualLeaveBalance);
    }

    // 사용자 연차 현황 조회
    @Transactional(readOnly = true)
    public List<AnnualLeaveBalanceResponseDto> getBalancesByUser(AuthenticatedUserDto authenticatedUser) {
        List<AnnualLeaveBalance> balances = annualLeaveBalanceRepository.findByUserId(authenticatedUser.getUserId());
        return balances.stream()
                .map(AnnualLeaveBalanceResponseDto::new)
                .toList();
    }

    // 연차 사용 처리
    @Transactional
    public AnnualLeaveBalanceResponseDto useLeave(AuthenticatedUserDto authenticatedUser, AnnualLeaveUsageRequestDto dto) {
        AnnualLeaveBalance balance = annualLeaveBalanceRepository.findByUserIdAndYear(authenticatedUser.getUserId(), dto.getYear())
                .orElseThrow(() -> new IllegalArgumentException("연차 정보가 존재하지 않습니다."));

        if (dto.getDays() == null || dto.getDays().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("사용 일수는 0보다 커야 합니다.");
        }

        if (balance.getRemaining().compareTo(dto.getDays()) < 0) {
            throw new IllegalArgumentException("잔여 연차가 부족합니다.");
        }

        balance.useLeave(dto.getDays());
        return new AnnualLeaveBalanceResponseDto(balance);
    }

    // 연차 복원 처리
    @Transactional
    public AnnualLeaveBalanceResponseDto rollbackLeave(AuthenticatedUserDto authenticatedUser, AnnualLeaveUsageRequestDto dto) {
        AnnualLeaveBalance balance = annualLeaveBalanceRepository.findByUserIdAndYear(authenticatedUser.getUserId(), dto.getYear())
                .orElseThrow(() -> new IllegalArgumentException("연차 정보가 존재하지 않습니다."));

        if (dto.getDays() == null || dto.getDays().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("복원 일수는 0보다 커야 합니다.");
        }

        if (balance.getUsed().compareTo(dto.getDays()) < 0) {
            throw new IllegalArgumentException("복원 일수가 사용 일수보다 많습니다.");
        }

        balance.rollbackLeave(dto.getDays());
        return new AnnualLeaveBalanceResponseDto(balance);
    }

    // 법정 기준 연차 프리뷰(본인)
    @Transactional(readOnly = true)
    public AnnualLeaveStatutoryPreviewDto previewStatutory(AuthenticatedUserDto authenticatedUser, LocalDate asOf) {
        User user = userRepository.findById(authenticatedUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        LocalDate hireDate = user.getHireDate();
        if (hireDate == null) {
            throw new IllegalStateException("입사일 정보가 없습니다.");
        }
        if (asOf == null) {
            asOf = LocalDate.now();
        }
        if (asOf.isBefore(hireDate)) {
            return new AnnualLeaveStatutoryPreviewDto(
                    user.getId(), hireDate, asOf, 0,
                    0,
                    "기준일이 입사일 이전입니다."
            );
        }

        int serviceYears = Period.between(hireDate, asOf).getYears();

        if (serviceYears < 1) {
            int monthlyAccrual = countMonthlyAccrualDays(hireDate, asOf);
            return new AnnualLeaveStatutoryPreviewDto(
                    user.getId(), hireDate, asOf, serviceYears,
                    monthlyAccrual,
                    "1년 미만: 월 1일 발생 합계"
            );
        }

        // 1년 이상: 정액 공식만 적용
        int annualGrant = computeAnnualGrantDays(serviceYears);
        return new AnnualLeaveStatutoryPreviewDto(
                user.getId(), hireDate, asOf, serviceYears,
                annualGrant,
                "1년 이상: 법정 정액 부여"
        );
    }

    // 법정 기준 연차 프리뷰(관리자)
    @Transactional(readOnly = true)
    public AnnualLeaveStatutoryPreviewDto previewStatutoryForUser(Long targetUserId, LocalDate asOf) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        LocalDate hireDate = user.getHireDate();
        if (hireDate == null) {
            throw new IllegalStateException("입사일 정보가 없습니다.");
        }
        if (asOf == null) {
            asOf = LocalDate.now();
        }
        if (asOf.isBefore(hireDate)) {
            return new AnnualLeaveStatutoryPreviewDto(
                    user.getId(), hireDate, asOf, 0,
                    0,
                    "기준일이 입사일 이전입니다."
            );
        }

        int serviceYears = Period.between(hireDate, asOf).getYears();

        if (serviceYears < 1) {
            int monthlyAccrual = countMonthlyAccrualDays(hireDate, asOf);
            return new AnnualLeaveStatutoryPreviewDto(
                    user.getId(), hireDate, asOf, serviceYears,
                    monthlyAccrual,
                    "1년 미만: 월 1일 발생 합계"
            );
        }

        int annualGrant = computeAnnualGrantDays(serviceYears);
        return new AnnualLeaveStatutoryPreviewDto(
                user.getId(), hireDate, asOf, serviceYears,
                annualGrant,
                "1년 이상: 법정 정액 부여"
        );
    }

    private int computeAnnualGrantDays(int serviceYears) {
        int extra = (serviceYears - 1) / 2; // 2년마다 +1
        int grant = 15 + Math.max(0, extra);
        return Math.min(grant, 25);
    }

    private int countMonthlyAccrualDays(LocalDate hireDate, LocalDate asOf) { //입사일, 기준일
        int count = 0;
        for (int i = 1; i <= 11; i++) {
            LocalDate anniversary = hireDate.plusMonths(i);
            if (anniversary.isAfter(asOf)) {
                break;
            }
            // 1년 미만 기간 내에서만 카운트
            if (anniversary.isBefore(hireDate.plusYears(1))) {
                count++;
            }
        }
        return count;
    }

    
}
