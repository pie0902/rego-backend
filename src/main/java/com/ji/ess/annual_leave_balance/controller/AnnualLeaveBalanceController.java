package com.ji.ess.annual_leave_balance.controller;

import com.ji.ess.auth.dto.AuthenticatedUserDto;
import com.ji.ess.auth.security.CustomUserDetails;
import com.ji.ess.annual_leave_balance.dto.AnnualLeaveBalanceRequestDto;
import com.ji.ess.annual_leave_balance.dto.AnnualLeaveBalanceResponseDto;
import com.ji.ess.annual_leave_balance.dto.AnnualLeaveUsageRequestDto;
import com.ji.ess.annual_leave_balance.dto.AnnualLeaveStatutoryPreviewDto;
import com.ji.ess.annual_leave_balance.service.AnnualLeaveBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// 연차 관리 API 컨트롤러
@RestController
@RequestMapping("/api/annual-leave")
@Tag(name = "Annual Leave Balance", description = "연차 부여 및 사용 관리 API")
public class AnnualLeaveBalanceController {

    private final AnnualLeaveBalanceService annualLeaveBalanceService;

    public AnnualLeaveBalanceController(AnnualLeaveBalanceService annualLeaveBalanceService) {
        this.annualLeaveBalanceService = annualLeaveBalanceService;
    }

    // 연차 부여 생성
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "연차 부여", description = "대표 또는 관리자가 사원에게 연차 일수를 부여합니다.")
    public AnnualLeaveBalanceResponseDto createBalance(@RequestBody AnnualLeaveBalanceRequestDto dto) {
        return annualLeaveBalanceService.createBalance(dto);
    }

    // 내 연차 현황 조회
    @GetMapping("/me")
    @Operation(summary = "내 연차 조회", description = "로그인한 사용자의 연차 잔여량을 확인합니다.")
    public List<AnnualLeaveBalanceResponseDto> getMyBalances(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return annualLeaveBalanceService.getBalancesByUser(authenticatedUser);
    }

    // 연차 사용 처리
    @PostMapping("/use")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "연차 사용", description = "대표 또는 관리자가 사원의 연차를 차감합니다.")
    public AnnualLeaveBalanceResponseDto useLeave(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestBody AnnualLeaveUsageRequestDto dto) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return annualLeaveBalanceService.useLeave(authenticatedUser, dto);
    }

    // 연차 사용 복원 처리
    @PostMapping("/rollback")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "연차 복원", description = "대표 또는 관리자가 차감한 연차를 되돌립니다.")
    public AnnualLeaveBalanceResponseDto rollbackLeave(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                       @RequestBody AnnualLeaveUsageRequestDto dto) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return annualLeaveBalanceService.rollbackLeave(authenticatedUser, dto);
    }

    // 법정 기준 연차 프리뷰(본인)
    @GetMapping("/statutory/me")
    @Operation(summary = "법정 기준 연차 프리뷰", description = "입사기념일 기준으로 권고 부여일수를 계산합니다. asOf 미지정 시 오늘 기준")
    public AnnualLeaveStatutoryPreviewDto previewStatutory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "기준일(asOf) (미지정 시 오늘)", example = "2025-12-29")
            @RequestParam(value = "asOf", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        LocalDate targetDate = asOf != null ? asOf : LocalDate.now();
        return annualLeaveBalanceService.previewStatutory(authenticatedUser, targetDate);
    }

    // 법정 기준 연차 프리뷰(관리자)
    @GetMapping("/statutory")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "법정 기준 연차 프리뷰(관리자)", description = "특정 userId 기준으로 권고 부여일수를 계산합니다.")
    public AnnualLeaveStatutoryPreviewDto previewStatutoryForUser(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam("userId") Long userId,
            @Parameter(description = "기준일(asOf) (미지정 시 오늘)", example = "2025-12-29")
            @RequestParam(value = "asOf", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        LocalDate targetDate = asOf != null ? asOf : LocalDate.now();
        return annualLeaveBalanceService.previewStatutoryForUser(userId, targetDate);
    }
}
