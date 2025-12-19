package com.ji.ess.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthRequestDto {
    private String loginId;
    private String password;
}
// 로그인 요청 DTO
