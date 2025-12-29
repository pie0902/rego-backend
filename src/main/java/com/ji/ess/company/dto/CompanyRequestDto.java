package com.ji.ess.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "회사 등록 요청")
public class CompanyRequestDto {
    @Schema(description = "회사명", example = "ThunderDev")
    private String companyName;
    @Schema(description = "사업자등록번호", example = "123-45-67890")
    private String businessNumber;
    @Schema(description = "회사 주소", example = "Seoul, Korea")
    private String address;
    @Schema(description = "대표자명", example = "홍길동")
    private String ceoName;
    @Schema(description = "회사 이메일", example = "contact@thunderdev.site")
    private String email;
}
