package com.ji.ess.attendance.service;

import com.ji.ess.auth.dto.AuthenticatedUserDto;
import com.ji.ess.attendance.dto.AttendanceResponseDto;
import com.ji.ess.attendance.entity.Attendance;
import com.ji.ess.attendance.repository.AttendanceRepository;
import com.ji.ess.company.entity.Company;
import com.ji.ess.companyRule.entity.CompanyRule;
import com.ji.ess.companyRule.repository.CompanyRuleRepository;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// 근태 도메인 서비스
@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final CompanyRuleRepository companyRuleRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             CompanyRuleRepository companyRuleRepository,
                             UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.companyRuleRepository = companyRuleRepository;
        this.userRepository = userRepository;
    }

    // 사용자 근태 목록 조회
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAttendanceByUser(AuthenticatedUserDto authenticatedUser) {
        List<Attendance> attendances = attendanceRepository.findByUserId(authenticatedUser.getUserId());
        return attendances.stream()
                .map(AttendanceResponseDto::new)
                .toList();
    }

    // 출근 처리
    @Transactional
    public AttendanceResponseDto checkIn(AuthenticatedUserDto authenticatedUser) {
        User user = requireUser(authenticatedUser.getUserId());
        Company company = requireCompany(user);
        CompanyRule rule = requireCompanyRule(company.getId());
        LocalDate today = LocalDate.now();
        boolean alreadyCheckedIn = attendanceRepository.existsByUserIdAndWorkDate(user.getId(), today);
        if (alreadyCheckedIn) {
            throw new IllegalStateException("이미 오늘 출근했습니다.");
        }

        Attendance attendance = Attendance.checkIn(user, company, rule);
        attendanceRepository.save(attendance);
        return new AttendanceResponseDto(attendance);
    }

    // 퇴근 처리
    @Transactional
    public AttendanceResponseDto checkOut(AuthenticatedUserDto authenticatedUser) {
        User user = requireUser(authenticatedUser.getUserId());
        Company company = requireCompany(user);
        CompanyRule rule = requireCompanyRule(company.getId());

        Attendance attendance = attendanceRepository
                .findByUserIdAndWorkDate(user.getId(), LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("오늘 출근 기록이 없습니다."));

        attendance.checkOut(rule);
        attendanceRepository.save(attendance);
        return new AttendanceResponseDto(attendance);
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private Company requireCompany(User user) {
        Company company = user.getCompany();
        if (company == null) {
            throw new IllegalStateException("사용자에게 회사가 연결되어 있지 않습니다.");
        }
        return company;
    }

    private CompanyRule requireCompanyRule(Long companyId) {
        return companyRuleRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("근무 규칙이 설정되지 않았습니다."));
    }
}
