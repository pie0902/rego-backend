package com.ji.ess.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 사원 회원가입 요청 DTO
@Getter
@NoArgsConstructor
public class EmployeeRequestDto {

    private String loginId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String department;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String position;
    private LocalDate hireDate;
    private Long companyId;

    public UserRequestDto toUserRequestDto() {
        return UserRequestDto.fromEmployee(this);
    }
}
