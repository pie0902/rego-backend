package com.ji.ess.dashboard.controller;

import com.ji.ess.auth.dto.AuthenticatedUserDto;
import com.ji.ess.auth.security.CustomUserDetails;
import com.ji.ess.dashboard.dto.DashboardResponseDto;
import com.ji.ess.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 대시보드 API 컨트롤러
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "대시보드 통합 조회 API")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // 내 대시보드 조회
    @GetMapping("/me")
    @Operation(summary = "내 대시보드 조회", description = "로그인한 사용자의 프로필/근태/연차/휴가 정보를 한 번에 조회합니다.")
    public DashboardResponseDto getMyDashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return dashboardService.getDashboard(authenticatedUser);
    }
}
