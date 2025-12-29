package com.ji.ess.leave_request.dto;

import com.ji.ess.company.entity.Company;
import com.ji.ess.leave_request.entity.LeaveRequest;
import com.ji.ess.leave_request.entity.LeaveStatus;
import com.ji.ess.leave_request.entity.LeaveType;
import com.ji.ess.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 휴가 신청 응답 DTO
@NoArgsConstructor
@Getter
@Schema(description = "휴가 신청 응답")
public class LeaveRequestResponseDto {
    @Schema(description = "휴가신청 ID", example = "1")
    private Long leaveRequestId;
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;
    @Schema(description = "회사 ID", example = "1")
    private Long companyId;
    @Schema(description = "회사명", example = "ThunderDev")
    private String companyName;
    @Schema(description = "휴가 시작일", type = "string", format = "date", example = "2025-01-10")
    private LocalDate startDate;
    @Schema(description = "휴가 종료일", type = "string", format = "date", example = "2025-01-10")
    private LocalDate endDate;
    @Schema(description = "휴가 유형", example = "FULL_DAY")
    private LeaveType type;
    @Schema(description = "휴가 상태", example = "PENDING")
    private LeaveStatus status;
    @Schema(description = "사유", example = "개인 사정")
    private String reason;
    @Schema(description = "신청 일시", type = "string", format = "date-time", example = "2025-01-01T09:00:00")
    private LocalDateTime requestedAt;
    @Schema(description = "승인/거절 일시", type = "string", format = "date-time", example = "2025-01-01T10:00:00")
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
