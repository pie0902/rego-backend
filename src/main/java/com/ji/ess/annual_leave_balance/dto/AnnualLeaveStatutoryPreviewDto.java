package com.ji.ess.annual_leave_balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "법정 기준 연차 프리뷰 응답")
public class AnnualLeaveStatutoryPreviewDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "입사일", type = "string", format = "date", example = "2024-01-01")
    private LocalDate hireDate;
    @Schema(description = "기준일(asOf)", type = "string", format = "date", example = "2025-12-29")
    private LocalDate asOf;
    @Schema(description = "근속 연수", example = "1")
    private int serviceYears;
    @Schema(description = "권고 부여일수", example = "15")
    private int recommendedGranted;
    @Schema(description = "참고 사항", example = "법정 기준에 따른 권고치입니다.")
    private String notes;

    public AnnualLeaveStatutoryPreviewDto(Long userId,
                                          LocalDate hireDate,
                                          LocalDate asOf,
                                          int serviceYears,
                                          int recommendedGranted,
                                          String notes) {
        this.userId = userId;
        this.hireDate = hireDate;
        this.asOf = asOf;
        this.serviceYears = serviceYears;
        this.recommendedGranted = recommendedGranted;
        this.notes = notes;
    }
}
// 법정 기준 연차 프리뷰 DTO
