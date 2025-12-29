package com.ji.ess.companyRule.dto;


import com.ji.ess.companyRule.entity.CompanyRule;
import com.ji.ess.companyRule.entity.WorkType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@ToString
@Schema(description = "근무 규칙 응답")
public class CompanyRuleResponseDto {
    @Schema(description = "회사 ID", example = "1")
    private Long companyId;
    @Schema(description = "회사명", example = "ThunderDev")
    private String companyName;
    @Schema(description = "근무 형태 (FIXED/FLEXIBLE)", example = "FIXED")
    private WorkType workType; // FIXED or FLEXIBLE

    @Schema(description = "고정 근무 출근 기준시간", type = "string", format = "time", example = "09:00:00")
    private LocalTime standardCheckIn;      // 고정 근무 출근 기준시간
    @Schema(description = "고정 근무 퇴근 기준시간", type = "string", format = "time", example = "18:00:00")
    private LocalTime standardCheckOut;     // 고정 근무 퇴근 기준시간
    @Schema(description = "유연근무 시 필수 근무시간", example = "8.0")
    private BigDecimal requiredHours;       // 유연근무 시 필수 근무시간
    @Schema(description = "지각 허용분(분)", example = "10")
    private Integer lateTolerance;          // 지각 허용분
    @Schema(description = "조퇴 허용분(분)", example = "10")
    private Integer earlyLeaveTolerance;    // 조퇴 허용분
    @Schema(description = "주말 근무 허용 여부", example = "false")
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
