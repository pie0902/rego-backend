package com.ji.ess.user.dto;

import com.ji.ess.company.dto.CompanyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

// CEO 회원가입 요청 DTO
@Getter
@NoArgsConstructor
public class CeoRegisterRequestDto {
    private CeoUserRequestDto user;
    private CompanyRequestDto company;
}
