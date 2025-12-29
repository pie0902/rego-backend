package com.ji.ess.annual_leave_balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;

// 연차 부여 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Jacksonized
@Schema(description = "연차 부여 요청")
public class AnnualLeaveBalanceRequestDto {
    @Schema(description = "대상 사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "연도", example = "2025")
    private int year;
    @Schema(description = "부여 일수", example = "15")
    private BigDecimal granted;
}
