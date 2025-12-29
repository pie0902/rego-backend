package com.ji.ess.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청")
public class AuthRequestDto {
    @Schema(description = "로그인 ID", example = "ceo1")
    private String loginId;
    @Schema(description = "비밀번호", example = "pass1234!")
    private String password;
}
// 로그인 요청 DTO
