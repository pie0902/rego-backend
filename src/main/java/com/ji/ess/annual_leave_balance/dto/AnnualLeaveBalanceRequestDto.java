package com.ji.ess.annual_leave_balance.dto;

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
public class AnnualLeaveBalanceRequestDto {
    private Long userId;
    private int year;
    private BigDecimal granted;
}
