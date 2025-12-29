package com.ji.ess.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 사용자 정보 수정 요청 DTO
@Getter
@NoArgsConstructor
@Schema(description = "내 정보 수정 요청")
public class UserUpdateRequestDto {
    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;
    @Schema(description = "이메일", example = "user@thunderdev.site")
    private String email;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    @Schema(description = "부서", example = "개발")
    private String department;
    @Schema(description = "직급", example = "사원")
    private String position;
    @Schema(description = "주소", example = "Seoul, Korea")
    private String address;
}
