package com.ji.ess.dashboard.dto;

import com.ji.ess.annual_leave_balance.dto.AnnualLeaveBalanceResponseDto;
import com.ji.ess.attendance.dto.AttendanceResponseDto;
import com.ji.ess.leave_request.dto.LeaveRequestResponseDto;
import com.ji.ess.user.dto.UserResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponseDto {
    private final UserResponseDto user;
    private final List<AttendanceResponseDto> attendance;
    private final List<AnnualLeaveBalanceResponseDto> annualLeaveBalances;
    private final List<LeaveRequestResponseDto> leaveRequests;
}
// 대시보드 응답 DTO
