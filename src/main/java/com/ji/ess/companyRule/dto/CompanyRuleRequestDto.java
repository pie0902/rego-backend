package com.ji.ess.companyRule.dto;


import com.ji.ess.companyRule.entity.WorkType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalTime;

// 근무 규칙 등록/수정 요청 DTO
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Jacksonized
@Schema(description = "근무 규칙 등록/수정 요청")
public class CompanyRuleRequestDto {
    @Schema(description = "회사 ID", example = "1")
    private Long companyId;

    @Schema(description = "근무 형태 (FIXED/FLEXIBLE)", example = "FIXED")
    private WorkType workType;

    @Schema(description = "고정 근무 출근 기준시간", type = "string", format = "time", example = "09:00:00")
    private LocalTime standardCheckIn;

    @Schema(description = "고정 근무 퇴근 기준시간", type = "string", format = "time", example = "18:00:00")
    private LocalTime standardCheckOut;

    @Schema(description = "유연근무 시 필수 근무시간", example = "8.0")
    private BigDecimal requiredHours;

    @Schema(description = "지각 허용분(분)", example = "10")
    private Integer lateTolerance;

    @Schema(description = "조퇴 허용분(분)", example = "10")
    private Integer earlyLeaveTolerance;

    @Schema(description = "주말 근무 허용 여부", example = "false")
    private Boolean weekendWorkAllowed;
}
