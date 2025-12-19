package com.ji.ess.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

// 사용자 등록 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Jacksonized
public class UserRequestDto {
    private String loginId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String department;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String position;
    private Long companyId; // 회사 ID만 받기
    private LocalDate hireDate;

    public static UserRequestDto fromCeoUser(CeoUserRequestDto dto) {
        return UserRequestDto.builder()
                .loginId(dto.getLoginId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .department(dto.getDepartment())
                .position(dto.getPosition())
                .hireDate(dto.getHireDate())
                .build();
    }

    public static UserRequestDto fromEmployee(EmployeeRequestDto dto) {
        return UserRequestDto.builder()
                .loginId(dto.getLoginId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .department(dto.getDepartment())
                .position(dto.getPosition())
                .companyId(dto.getCompanyId())
                .hireDate(dto.getHireDate())
                .build();
    }
}
