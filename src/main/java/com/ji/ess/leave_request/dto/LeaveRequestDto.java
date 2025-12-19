package com.ji.ess.leave_request.dto;


import com.ji.ess.leave_request.entity.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

// 휴가 신청 요청 DTO
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Jacksonized
public class LeaveRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType type;
    private String reason;
}
