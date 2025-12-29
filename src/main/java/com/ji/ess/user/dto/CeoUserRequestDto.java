package com.ji.ess.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// CEO 사용자 정보 요청 DTO
@Getter
@NoArgsConstructor
@Schema(description = "CEO 사용자 정보 요청")
public class CeoUserRequestDto {

    @Schema(description = "로그인 ID", example = "ceo1")
    private String loginId;
    @Schema(description = "사용자 이름", example = "대표")
    private String username;
    @Schema(description = "비밀번호", example = "pass1234!")
    private String password;
    @Schema(description = "이메일", example = "ceo@thunderdev.site")
    private String email;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    @Schema(description = "주소", example = "Seoul, Korea")
    private String address;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "부서(선택)", example = "경영")
    private String department;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "직급(선택)", example = "CEO")
    private String position;
    @Schema(description = "입사일", type = "string", format = "date", example = "2024-01-01")
    private LocalDate hireDate;
    public UserRequestDto toUserRequestDto() {
        return UserRequestDto.fromCeoUser(this);
    }
}
