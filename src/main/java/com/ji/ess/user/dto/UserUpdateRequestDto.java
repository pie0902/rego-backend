package com.ji.ess.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 사용자 정보 수정 요청 DTO
@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {
    private String username;
    private String email;
    private String phone;
    private String department;
    private String position;
    private String address;
}
