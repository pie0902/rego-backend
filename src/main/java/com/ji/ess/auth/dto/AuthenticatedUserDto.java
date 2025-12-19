package com.ji.ess.auth.dto;

import com.ji.ess.auth.security.CustomUserDetails;
import lombok.Getter;

@Getter
public class AuthenticatedUserDto {

    private final Long userId;

    // 생성자 방식으로 사용(정적 from 제거)
    public AuthenticatedUserDto(CustomUserDetails userDetails) {
        this.userId = userDetails.getId();
    }

    // 내부적으로 사용할 수 있는 식별자 기반 생성자(필요 시 확장)
    private AuthenticatedUserDto(Long userId) {
        this.userId = userId;
    }
}
// 인증 사용자 정보 DTO
