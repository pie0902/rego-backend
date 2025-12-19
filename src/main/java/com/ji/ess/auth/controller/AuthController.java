package com.ji.ess.auth.controller;

import com.ji.ess.auth.dto.AuthRequestDto;
import com.ji.ess.auth.dto.AuthResponseDto;
import com.ji.ess.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "로그인 및 토큰 발급 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto requestDto) {
        AuthResponseDto responseDto = authService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
