package com.ji.ess.leave_request.service;

import com.ji.ess.auth.dto.AuthenticatedUserDto;
import com.ji.ess.company.entity.Company;
import com.ji.ess.company.repository.CompanyRepository;
import com.ji.ess.leave_request.dto.LeaveRequestDto;
import com.ji.ess.leave_request.dto.LeaveRequestResponseDto;
import com.ji.ess.leave_request.dto.LeaveStatusUpdateDto;
import com.ji.ess.leave_request.entity.LeaveRequest;
import com.ji.ess.leave_request.entity.LeaveStatus;
import com.ji.ess.leave_request.entity.LeaveType;
import com.ji.ess.leave_request.repository.LeaveRequestRepository;
import com.ji.ess.annual_leave_balance.repository.AnnualLeaveBalanceRepository;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.entity.UserRole;
import com.ji.ess.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

// 휴가 신청 도메인 서비스
@Service
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AnnualLeaveBalanceRepository annualLeaveBalanceRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                               CompanyRepository companyRepository,
                               UserRepository userRepository,
                               AnnualLeaveBalanceRepository annualLeaveBalanceRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.annualLeaveBalanceRepository = annualLeaveBalanceRepository;
    }

    // 휴가 신청 생성 및 검증
    @Transactional
    public LeaveRequestResponseDto createLeaveRequest(AuthenticatedUserDto authenticatedUser, LeaveRequestDto dto) {
        User user = requireUser(authenticatedUser.getUserId());
        Company company = companyRepository.findById(requireCompanyId(user))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사입니다."));

        // 연차 잔여 체크 (0.5 지원). 조퇴/병가는 차감 없음.
        BigDecimal requiredDays = calculateRequiredDays(dto.getType(), dto.getStartDate(), dto.getEndDate());
        if (requiredDays.compareTo(BigDecimal.ZERO) > 0) {
            int year = validateSingleYear(dto.getStartDate(), dto.getEndDate());
            var balance = annualLeaveBalanceRepository.findByUserIdAndYear(user.getId(), year)
                    .orElseThrow(() -> new IllegalArgumentException("올해 연차 정보가 없습니다."));
            if (balance.getRemaining().compareTo(requiredDays) < 0) {
                throw new IllegalArgumentException("연차 잔여가 부족합니다.");
            }
        }
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .user(user)
                .company(company)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .type(dto.getType())
                .reason(dto.getReason())
                .requestedAt(LocalDateTime.now())
                .build();
        leaveRequestRepository.save(leaveRequest);
        return new LeaveRequestResponseDto(leaveRequest);
    }

    private int validateSingleYear(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("휴가 시작일/종료일이 필요합니다.");
        }
        if (start.getYear() != end.getYear()) {
            throw new IllegalArgumentException("휴가 기간은 동일 연도 내에서만 신청 가능합니다.");
        }
        return start.getYear();
    }

    private BigDecimal calculateRequiredDays(LeaveType type, LocalDate startDate, LocalDate endDate) {
        if (type == null) {
            throw new IllegalArgumentException("휴가 타입이 필요합니다.");
        }
        return switch (type) {
            case SICK, EARLY_LEAVE -> BigDecimal.ZERO; // 차감 없음
            case HALF_DAY -> {
                if (startDate == null || endDate == null || !startDate.equals(endDate)) {
                    throw new IllegalArgumentException("반차는 시작일과 종료일이 같아야 합니다.");
                }
                yield BigDecimal.valueOf(0.5);
            }
            case FULL_DAY -> BigDecimal.valueOf(countWeekdaysInclusive(startDate, endDate));
        };
    }

    private int countWeekdaysInclusive(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("휴가 시작일/종료일이 필요합니다.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("종료일이 시작일보다 빠릅니다.");
        }
        int count = 0;
        LocalDate d = start;
        while (!d.isAfter(end)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                count++;
            }
            d = d.plusDays(1);
        }
        return count;
    }

    // 회사별 대기 신청 조회
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getPendingRequests(AuthenticatedUserDto authenticatedUser) {
        User approver = requireUser(authenticatedUser.getUserId());
        validateApproverRole(approver);
        Long companyId = requireCompanyId(approver);
        List<LeaveRequest> requests = leaveRequestRepository
                .findByCompanyIdAndStatus(companyId, LeaveStatus.PENDING);
        return requests.stream()
                .map(LeaveRequestResponseDto::new)
                .toList();
    }

    // 내 휴가 신청 목록 조회
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getMyRequests(AuthenticatedUserDto authenticatedUser) {
        User user = requireUser(authenticatedUser.getUserId());
        List<LeaveRequest> requests = leaveRequestRepository
                .findByUserIdOrderByRequestedAtDesc(user.getId());
        return requests.stream()
                .map(LeaveRequestResponseDto::new)
                .toList();
    }

    // 휴가 승인/거절 처리
    @Transactional
    public LeaveRequestResponseDto updateStatus(AuthenticatedUserDto authenticatedUser, LeaveStatusUpdateDto dto) {
        User approver = requireUser(authenticatedUser.getUserId());
        LeaveRequest leave = leaveRequestRepository.findById(dto.getLeaveRequestId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 휴가 신청입니다."));

        validateApproverRole(approver);

        if (dto.getStatus() == LeaveStatus.APPROVED) {
            // 승인 전 잔여 재검증 및 차감 처리(0.5 지원)
            BigDecimal requiredDays = calculateRequiredDays(leave.getType(), leave.getStartDate(), leave.getEndDate());
            if (requiredDays.compareTo(BigDecimal.ZERO) > 0) {
                int year = validateSingleYear(leave.getStartDate(), leave.getEndDate());
                var balance = annualLeaveBalanceRepository.findByUserIdAndYear(leave.getUser().getId(), year)
                        .orElseThrow(() -> new IllegalArgumentException("올해 연차 정보가 없습니다."));
                if (balance.getRemaining().compareTo(requiredDays) < 0) {
                    throw new IllegalArgumentException("연차 잔여가 부족하여 승인할 수 없습니다.");
                }
                balance.useLeave(requiredDays);
            }
            leave.approve(approver);
        } else if (dto.getStatus() == LeaveStatus.REJECTED) {
            leave.reject(approver);
        } else {
            throw new IllegalArgumentException("잘못된 상태 변경 요청입니다.");
        }

        leaveRequestRepository.save(leave);
        return new LeaveRequestResponseDto(leave);
    }

    

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private void validateApproverRole(User approver) {
        if (approver.getRole() != UserRole.CEO && approver.getRole() != UserRole.MANAGER) {
            throw new IllegalArgumentException("승인 권한이 없습니다.");
        }
    }

    private Long requireCompanyId(User user) {
        Company company = user.getCompany();
        if (company == null) {
            throw new IllegalStateException("사용자에게 회사가 연결되어 있지 않습니다.");
        }
        return company.getId();
    }
}
