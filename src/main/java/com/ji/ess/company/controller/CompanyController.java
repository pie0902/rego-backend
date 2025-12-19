package com.ji.ess.company.controller;

import com.ji.ess.company.dto.CompanyRequestDto;
import com.ji.ess.company.dto.CompanyResponseDto;
import com.ji.ess.company.entity.Company;
import com.ji.ess.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 회사 관리 API 컨트롤러
@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company", description = "회사 정보 등록 및 조회 API")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }
    // 회사 등록
    @PostMapping("/create")
    @Operation(summary = "회사 등록", description = "회사 기본 정보를 등록합니다.")
    public CompanyResponseDto create(@RequestBody CompanyRequestDto companyRequestDto){
        Company company = companyService.createCompany(companyRequestDto);
        return new CompanyResponseDto(company);
    }
    // 전체 회사 목록 조회
    @GetMapping("/all")
    @Operation(summary = "회사 목록", description = "등록된 모든 회사 정보를 조회합니다.")
    public List<CompanyResponseDto> getAllCompany() {
        List<CompanyResponseDto> companies = companyService.getAllCompanies();
        return companies;
    }
    // 회사명 키워드 검색
    @GetMapping("/search")
    @Operation(summary = "회사명 검색", description = "회사명을 키워드로 검색합니다.")
    public List<CompanyResponseDto> searchCompanies(@RequestParam String name) {
        return companyService.searchCompaniesByName(name);
    }




    // 개발용 전체 삭제
    @DeleteMapping("/deleteAll")
    @Operation(summary = "모든 회사 삭제", description = "개발용 도구입니다. 모든 회사 데이터를 삭제합니다.")
    public String deleteAllCompanies() {
        companyService.deleteAllCompanies();
        return "All companies deleted successfully.";
    }
    // 개발용 초기화
    @DeleteMapping("/truncate")
    @Operation(summary = "회사 초기화", description = "개발용 도구입니다. 회사 데이터를 삭제하고 ID를 초기화합니다.")
    public String truncateAllCompanies() {
        companyService.truncateAllCompanies();
        return "All companies truncated and IDs reset.";
    }


}
