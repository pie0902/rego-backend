package com.ji.ess.companyRule.controller;


import com.ji.ess.companyRule.dto.CompanyRuleRequestDto;
import com.ji.ess.companyRule.dto.CompanyRuleResponseDto;
import com.ji.ess.companyRule.service.CompanyRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/CompanyRule")
@Tag(name = "Company Rule", description = "근무 규칙 등록 및 수정 API")
public class CompanyRuleController {
    private CompanyRuleService companyRuleService;
    public CompanyRuleController(CompanyRuleService companyRuleService) {
        this.companyRuleService = companyRuleService;
    }
    @PostMapping("/createRule")
    @Operation(summary = "규칙 등록", description = "회사의 근무 규칙을 새로 등록합니다.")
    public CompanyRuleResponseDto createRule(@RequestBody CompanyRuleRequestDto dto) {
        return companyRuleService.createRule(dto);
    }
    @GetMapping("/get")
    @Operation(summary = "규칙 조회", description = "회사 ID로 근무 규칙을 조회합니다.")
    public CompanyRuleResponseDto getRules(@Parameter(description = "회사 ID", example = "1") @RequestParam Long companyId) {
        return companyRuleService.getRules(companyId);
    }
    @PutMapping("/update")
    @Operation(summary = "규칙 수정", description = "회사 근무 규칙을 수정합니다.")
    public CompanyRuleResponseDto updateRule(@RequestBody CompanyRuleRequestDto dto) {
        return companyRuleService.updateRules(dto);
    }
}
