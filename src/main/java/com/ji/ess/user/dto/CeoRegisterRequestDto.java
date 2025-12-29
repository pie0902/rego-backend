package com.ji.ess.user.dto;

import com.ji.ess.company.dto.CompanyRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

// CEO 회원가입 요청 DTO
@Getter
@NoArgsConstructor
@Schema(description = "CEO 회원가입 요청(회사 + 대표 계정 동시 생성)")
public class CeoRegisterRequestDto {
    @Schema(description = "대표(CEO) 사용자 정보")
    private CeoUserRequestDto user;
    @Schema(description = "회사 정보")
    private CompanyRequestDto company;
}
