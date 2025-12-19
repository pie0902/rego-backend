package com.ji.ess.companyRule.service;

import com.ji.ess.company.entity.Company;
import com.ji.ess.company.repository.CompanyRepository;
import com.ji.ess.companyRule.dto.CompanyRuleRequestDto;
import com.ji.ess.companyRule.dto.CompanyRuleResponseDto;
import com.ji.ess.companyRule.entity.CompanyRule;
import com.ji.ess.companyRule.repository.CompanyRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 근무 규칙 도메인 서비스
@Service
public class CompanyRuleService {
    private final CompanyRuleRepository companyRuleRepository;
    private final CompanyRepository companyRepository;

    public CompanyRuleService(CompanyRuleRepository companyRuleRepository,
                              CompanyRepository companyRepository) {
        this.companyRuleRepository = companyRuleRepository;
        this.companyRepository = companyRepository;
    }
    // 규칙 생성
    public CompanyRuleResponseDto createRule(CompanyRuleRequestDto companyRuleRequestDto) {
        Company company = companyRepository.findById(companyRuleRequestDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사입니다."));
        if (companyRuleRepository.findByCompanyId(companyRuleRequestDto.getCompanyId()).isPresent()) {
            throw new IllegalStateException("이미 해당 회사의 규칙이 존재합니다.");
        }
        CompanyRule companyRule = CompanyRule.builder()
                .company(company)
                .workType(companyRuleRequestDto.getWorkType())
                .standardCheckIn(companyRuleRequestDto.getStandardCheckIn())
                .standardCheckOut(companyRuleRequestDto.getStandardCheckOut())
                .requiredHours(companyRuleRequestDto.getRequiredHours())
                .lateTolerance(companyRuleRequestDto.getLateTolerance())
                .earlyLeaveTolerance(companyRuleRequestDto.getEarlyLeaveTolerance())
                .weekendWorkAllowed(companyRuleRequestDto.getWeekendWorkAllowed())
                .build();
        companyRuleRepository.save(companyRule);
        CompanyRuleResponseDto companyRuleResponseDto = new CompanyRuleResponseDto(companyRule);
        return companyRuleResponseDto;
    }
    // 규칙 조회
    @Transactional(readOnly = true)
    public CompanyRuleResponseDto getRules(Long companyId) {
        CompanyRule companyRule = companyRuleRepository
                .findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회사의 규칙이 존재하지 않습니다."));

        CompanyRuleResponseDto companyRuleResponseDto = new CompanyRuleResponseDto(companyRule);
        return companyRuleResponseDto;
    }
    // 규칙 수정
    public CompanyRuleResponseDto updateRules(CompanyRuleRequestDto companyRuleRequestDto) {
        CompanyRule companyRule = companyRuleRepository
                .findByCompanyId(companyRuleRequestDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회사의 규칙이 존재하지 않습니다."));
        companyRule.update(
                companyRuleRequestDto.getWorkType(),
                companyRuleRequestDto.getStandardCheckIn(),
                companyRuleRequestDto.getStandardCheckOut(),
                companyRuleRequestDto.getRequiredHours(),
                companyRuleRequestDto.getLateTolerance(),
                companyRuleRequestDto.getEarlyLeaveTolerance(),
                companyRuleRequestDto.getWeekendWorkAllowed()
        );
        companyRuleRepository.save(companyRule);
        CompanyRuleResponseDto companyRuleResponseDto = new CompanyRuleResponseDto(companyRule);
        return companyRuleResponseDto;
    }

    

}
