package com.ji.ess.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 사원 회원가입 요청 DTO
@Getter
@NoArgsConstructor
@Schema(description = "사원 회원가입 요청")
public class EmployeeRequestDto {

    @Schema(description = "로그인 ID", example = "emp1")
    private String loginId;
    @Schema(description = "사용자 이름", example = "사원")
    private String username;
    @Schema(description = "비밀번호", example = "pass1234!")
    private String password;
    @Schema(description = "이메일", example = "emp1@thunderdev.site")
    private String email;
    @Schema(description = "전화번호", example = "010-0000-0000")
    private String phone;
    @Schema(description = "주소", example = "Seoul, Korea")
    private String address;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "부서(선택)", example = "개발")
    private String department;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "직급(선택)", example = "EMPLOYEE")
    private String position;
    @Schema(description = "입사일", type = "string", format = "date", example = "2024-01-01")
    private LocalDate hireDate;
    @Schema(description = "회사 ID", example = "1")
    private Long companyId;

    public UserRequestDto toUserRequestDto() {
        return UserRequestDto.fromEmployee(this);
    }
}
