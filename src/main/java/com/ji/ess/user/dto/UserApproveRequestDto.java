package com.ji.ess.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사원 승인 요청")
public class UserApproveRequestDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "부서(선택)", example = "개발")
    private String department;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "직급(선택)", example = "사원")
    private String position;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "역할(선택) (CEO/MANAGER/EMPLOYEE)", example = "EMPLOYEE")
    private String role;
}
// 사원 승인 요청 DTO
