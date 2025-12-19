package com.ji.ess.dashboard.service;

import com.ji.ess.annual_leave_balance.dto.AnnualLeaveBalanceResponseDto;
import com.ji.ess.annual_leave_balance.service.AnnualLeaveBalanceService;
import com.ji.ess.attendance.dto.AttendanceResponseDto;
import com.ji.ess.attendance.service.AttendanceService;
import com.ji.ess.auth.dto.AuthenticatedUserDto;
import com.ji.ess.dashboard.dto.DashboardResponseDto;
import com.ji.ess.leave_request.dto.LeaveRequestResponseDto;
import com.ji.ess.leave_request.service.LeaveRequestService;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 대시보드 통합 조회 서비스
@Service
public class DashboardService {

    private final UserService userService;
    private final AttendanceService attendanceService;
    private final AnnualLeaveBalanceService annualLeaveBalanceService;
    private final LeaveRequestService leaveRequestService;

    public DashboardService(UserService userService,
                            AttendanceService attendanceService,
                            AnnualLeaveBalanceService annualLeaveBalanceService,
                            LeaveRequestService leaveRequestService) {
        this.userService = userService;
        this.attendanceService = attendanceService;
        this.annualLeaveBalanceService = annualLeaveBalanceService;
        this.leaveRequestService = leaveRequestService;
    }

    // 대시보드 데이터 집계 조회
    @Transactional(readOnly = true)
    public DashboardResponseDto getDashboard(AuthenticatedUserDto authenticatedUser) {
        User user = userService.getUserById(authenticatedUser.getUserId());
        List<AttendanceResponseDto> attendance = attendanceService.getAttendanceByUser(authenticatedUser);
        List<AnnualLeaveBalanceResponseDto> annualLeaves = annualLeaveBalanceService.getBalancesByUser(authenticatedUser);
        List<LeaveRequestResponseDto> leaveRequests = leaveRequestService.getMyRequests(authenticatedUser);

        return DashboardResponseDto.builder()
                .user(userService.toResponseDto(user))
                .attendance(attendance)
                .annualLeaveBalances(annualLeaves)
                .leaveRequests(leaveRequests)
                .build();
    }
}
