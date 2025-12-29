package com.ji.ess.leave_request.dto;

import com.ji.ess.leave_request.entity.LeaveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "휴가 상태 변경 요청")
public class LeaveStatusUpdateDto {
    @Schema(description = "변경할 휴가신청 ID", example = "1")
    private Long leaveRequestId;   // 변경할 휴가신청 id
    @Schema(description = "변경할 상태 (APPROVED/REJECTED)", example = "APPROVED")
    private LeaveStatus status;    // 바꿀 상태 (APPROVED / REJECTED)
}
