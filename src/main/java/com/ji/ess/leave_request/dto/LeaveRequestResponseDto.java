package com.ji.ess.leave_request.dto;

import com.ji.ess.company.entity.Company;
import com.ji.ess.leave_request.entity.LeaveRequest;
import com.ji.ess.leave_request.entity.LeaveStatus;
import com.ji.ess.leave_request.entity.LeaveType;
import com.ji.ess.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 휴가 신청 응답 DTO
@NoArgsConstructor
@Getter
public class LeaveRequestResponseDto {
    private Long leaveRequestId;
    private Long userId;
    private String userName;
    private Long companyId;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType type;
    private LeaveStatus status;
    private String reason;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    public LeaveRequestResponseDto(LeaveRequest leaveRequest){
        this.leaveRequestId = leaveRequest.getId();
        this.userId = leaveRequest.getUser().getId();
        this.userName = leaveRequest.getUser().getUsername();
        this.companyId = leaveRequest.getCompany().getId();
        this.companyName = leaveRequest.getCompany().getCompanyName();
        this.startDate = leaveRequest.getStartDate();
        this.endDate = leaveRequest.getEndDate();
        this.type = leaveRequest.getType();
        this.status = leaveRequest.getStatus();
        this.reason = leaveRequest.getReason();
        this.requestedAt = leaveRequest.getRequestedAt();
        this.approvedAt = leaveRequest.getApprovedAt();
    }
}
