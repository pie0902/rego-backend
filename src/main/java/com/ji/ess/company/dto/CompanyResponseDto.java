package com.ji.ess.company.dto;

import com.ji.ess.company.entity.Company;
import lombok.Getter;

// 회사 응답 DTO
@Getter
public class CompanyResponseDto {
    private String companyName;
    private String businessNumber;
    private String address;
    private String ceoName;
    private String email;
    public CompanyResponseDto(Company company){
        this.companyName = company.getCompanyName();
        this.businessNumber = company.getBusinessNumber();
        this.address = company.getAddress();
        this.ceoName = company.getCeoName();
        this.email = company.getEmail();
    }
}
