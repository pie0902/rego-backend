package com.ji.ess.leave_request.dto;


import com.ji.ess.leave_request.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

// 휴가 신청 요청 DTO
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Jacksonized
@Schema(description = "휴가 신청 요청")
public class LeaveRequestDto {
    @Schema(description = "휴가 시작일", type = "string", format = "date", example = "2025-01-10")
    private LocalDate startDate;
    @Schema(description = "휴가 종료일", type = "string", format = "date", example = "2025-01-10")
    private LocalDate endDate;
    @Schema(description = "휴가 유형", example = "FULL_DAY")
    private LeaveType type;
    @Schema(description = "사유", example = "개인 사정")
    private String reason;
}
