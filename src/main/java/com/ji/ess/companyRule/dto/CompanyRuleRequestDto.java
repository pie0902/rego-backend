package com.ji.ess.companyRule.dto;


import com.ji.ess.companyRule.entity.WorkType;
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
public class CompanyRuleRequestDto {
    private Long companyId;  //
    private WorkType workType; // FIXED or FLEXIBLE
    private LocalTime standardCheckIn;      // 고정 근무 출근 기준시간
    private LocalTime standardCheckOut;     // 고정 근무 퇴근 기준시간
    private BigDecimal requiredHours;       // 유연근무 시 필수 근무시간
    private Integer lateTolerance;          // 지각 허용분
    private Integer earlyLeaveTolerance;    // 조퇴 허용분
    private Boolean weekendWorkAllowed;     // 주말 근무 허용 여부
}
