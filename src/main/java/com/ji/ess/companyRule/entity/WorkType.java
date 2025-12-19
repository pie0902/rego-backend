package com.ji.ess.companyRule.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

// 근무 형태 열거형
public enum WorkType {
    FIXED,    // 고정 출퇴근 (예: 09~18)
    FLEXIBLE; // 유연근무 (총 8시간 기준)
    @JsonCreator
    public static WorkType from(String value) {
        return WorkType.valueOf(value.toUpperCase());
    }
}
