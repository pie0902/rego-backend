package com.ji.ess.annual_leave_balance.dto;

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
public class AnnualLeaveUsageRequestDto {
    private int year;
    private BigDecimal days; // 0.5 지원
}
