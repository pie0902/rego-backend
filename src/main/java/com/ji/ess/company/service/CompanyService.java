package com.ji.ess.company.service;


import com.ji.ess.company.dto.CompanyRequestDto;
import com.ji.ess.company.dto.CompanyResponseDto;
import com.ji.ess.company.entity.Company;
import com.ji.ess.company.repository.CompanyRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// 회사 도메인 서비스
@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    // 회사 생성 및 저장
    public Company createCompany(CompanyRequestDto dto) {
        Company company = Company.builder()
                .companyName(dto.getCompanyName())
                .businessNumber(dto.getBusinessNumber())
                .address(dto.getAddress())
                .ceoName(dto.getCeoName())
                .email(dto.getEmail())
                .build();
        return companyRepository.save(company);
    }
    // 전체 회사 목록 조회
    @Transactional(readOnly = true)
    public List<CompanyResponseDto> getAllCompanies() {
        List<Company> companyList = companyRepository.findAll();
        List<CompanyResponseDto> companies = new ArrayList<>();
        for (Company company : companyList) {
            CompanyResponseDto companyResponseDto = new CompanyResponseDto(company);
            companies.add(companyResponseDto);
        }
        return companies;
    }
    // 회사명 키워드 검색
    @Transactional(readOnly = true)
    public List<CompanyResponseDto> searchCompaniesByName(String name) {
        return companyRepository.findByCompanyNameContaining(name)
                .stream()
                .map(CompanyResponseDto::new)
                .toList();
    }

    // 개발용 전체 삭제
    @Transactional
    public void deleteAllCompanies() {
        companyRepository.deleteAllCompanies();
    }
    // 개발용 테이블 초기화
    @Transactional
    public void truncateAllCompanies() {
        companyRepository.truncateCompanies();
    }
}
