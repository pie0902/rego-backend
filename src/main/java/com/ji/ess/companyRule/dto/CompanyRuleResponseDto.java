package com.ji.ess.companyRule.dto;


import com.ji.ess.companyRule.entity.CompanyRule;
import com.ji.ess.companyRule.entity.WorkType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@ToString
public class CompanyRuleResponseDto {
    private Long companyId;
    private String companyName;
    private WorkType workType; // FIXED or FLEXIBLE

    private LocalTime standardCheckIn;      // 고정 근무 출근 기준시간
    private LocalTime standardCheckOut;     // 고정 근무 퇴근 기준시간
    private BigDecimal requiredHours;       // 유연근무 시 필수 근무시간
    private Integer lateTolerance;          // 지각 허용분
    private Integer earlyLeaveTolerance;    // 조퇴 허용분
    private Boolean weekendWorkAllowed;     // 주말 근무 허용 여부

    public CompanyRuleResponseDto(CompanyRule companyRule) {
        this.companyId = companyRule.getCompany() != null ? companyRule.getCompany().getId() : null;
        this.companyName = companyRule.getCompany() != null ? companyRule.getCompany().getCompanyName() : null;
        this.workType = companyRule.getWorkType();
        this.standardCheckIn = companyRule.getStandardCheckIn();
        this.standardCheckOut = companyRule.getStandardCheckOut();
        this.requiredHours = companyRule.getRequiredHours();
        this.lateTolerance = companyRule.getLateTolerance();
        this.earlyLeaveTolerance = companyRule.getEarlyLeaveTolerance();
        this.weekendWorkAllowed = companyRule.getWeekendWorkAllowed();
    }
}
// 근무 규칙 응답 DTO
