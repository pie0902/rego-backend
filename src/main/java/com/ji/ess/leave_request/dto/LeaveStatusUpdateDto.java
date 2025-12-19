package com.ji.ess.leave_request.dto;

import com.ji.ess.leave_request.entity.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

// 휴가 상태 변경 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Jacksonized
public class LeaveStatusUpdateDto {
    private Long leaveRequestId;   // 변경할 휴가신청 id
    private LeaveStatus status;    // 바꿀 상태 (APPROVED / REJECTED)
}
