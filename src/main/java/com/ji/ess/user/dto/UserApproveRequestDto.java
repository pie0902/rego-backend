package com.ji.ess.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserApproveRequestDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String department;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String position;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String role;
}
// 사원 승인 요청 DTO
