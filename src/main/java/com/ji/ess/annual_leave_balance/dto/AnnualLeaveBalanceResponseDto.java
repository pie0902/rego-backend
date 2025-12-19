package com.ji.ess.annual_leave_balance.dto;

import com.ji.ess.annual_leave_balance.entity.AnnualLeaveBalance;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class AnnualLeaveBalanceResponseDto {
    private Long id;
    private Long userId;
    private int year;
    private BigDecimal granted;
    private BigDecimal used;
    private BigDecimal remaining;

    public AnnualLeaveBalanceResponseDto(AnnualLeaveBalance balance) {
        this.id = balance.getId();
        this.userId = balance.getUser().getId();
        this.year = balance.getYear();
        this.granted = balance.getGranted();
        this.used = balance.getUsed();
        this.remaining = balance.getRemaining();
    }
}
// 연차 잔액 응답 DTO
