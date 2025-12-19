package com.ji.ess.auth.dto;

import lombok.Getter;

@Getter
public class AuthResponseDto {
    private final String token;
    private final String tokenType;

    public AuthResponseDto(String token) {
        this.token = token;
        this.tokenType = "Bearer";
    }
}
// 로그인 응답 DTO(토큰)
