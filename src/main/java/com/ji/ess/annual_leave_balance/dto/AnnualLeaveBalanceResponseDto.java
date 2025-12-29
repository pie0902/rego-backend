package com.ji.ess.annual_leave_balance.dto;

import com.ji.ess.annual_leave_balance.entity.AnnualLeaveBalance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Schema(description = "연차 잔액 응답")
public class AnnualLeaveBalanceResponseDto {
    @Schema(description = "연차 잔액 ID", example = "1")
    private Long id;
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "연도", example = "2025")
    private int year;
    @Schema(description = "부여 일수", example = "15")
    private BigDecimal granted;
    @Schema(description = "사용 일수", example = "3.5")
    private BigDecimal used;
    @Schema(description = "잔여 일수", example = "11.5")
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
