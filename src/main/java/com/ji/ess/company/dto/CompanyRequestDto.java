package com.ji.ess.company.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;


// 회사 등록 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@Builder
public class CompanyRequestDto {
    private String companyName;
    private String businessNumber;
    private String address;
    private String ceoName;
    private String email;
}
