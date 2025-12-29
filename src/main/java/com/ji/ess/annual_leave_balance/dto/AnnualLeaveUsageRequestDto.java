package com.ji.ess.annual_leave_balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;

// 연차 사용/복원 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Jacksonized
@Schema(description = "연차 사용/복원 요청")
public class AnnualLeaveUsageRequestDto {
    @Schema(description = "연도", example = "2025")
    private int year;
    @Schema(description = "차감/복원 일수 (0.5 지원)", example = "0.5")
    private BigDecimal days; // 0.5 지원
}
