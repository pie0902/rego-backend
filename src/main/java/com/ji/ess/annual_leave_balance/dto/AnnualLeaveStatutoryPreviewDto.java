package com.ji.ess.annual_leave_balance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AnnualLeaveStatutoryPreviewDto {
    private Long userId;
    private LocalDate hireDate;
    private LocalDate asOf;
    private int serviceYears;
    private int recommendedGranted;
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
