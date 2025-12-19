package com.ji.ess.auth.service;

import com.ji.ess.auth.dto.AuthRequestDto;
import com.ji.ess.auth.dto.AuthResponseDto;
import com.ji.ess.global.jwt.JwtTokenProvider;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 인증 서비스
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    // 로그인 처리 및 토큰 발급
    public AuthResponseDto login(AuthRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getLoginId(), requestDto.getPassword())
        );

        User user = userRepository.findByLoginId(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtTokenProvider.generateToken(user.getLoginId(), user.getRole());
        return new AuthResponseDto(token);
    }
}
