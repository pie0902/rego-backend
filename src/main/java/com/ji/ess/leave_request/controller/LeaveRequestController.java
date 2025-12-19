package com.ji.ess.leave_request.controller;

import com.ji.ess.auth.dto.AuthenticatedUserDto;
import com.ji.ess.auth.security.CustomUserDetails;
import com.ji.ess.leave_request.dto.LeaveRequestDto;
import com.ji.ess.leave_request.dto.LeaveRequestResponseDto;
import com.ji.ess.leave_request.dto.LeaveStatusUpdateDto;
import com.ji.ess.leave_request.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 휴가 신청 관리 API 컨트롤러
@RestController
@RequestMapping("/api/leaveRequest")
@Tag(name = "Leave Request", description = "휴가 신청과 승인 관련 API")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;
    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }
    // 휴가 신청 생성
    @PostMapping("/create")
    @Operation(summary = "휴가 신청", description = "로그인한 사원이 휴가를 신청합니다.")
    public LeaveRequestResponseDto create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody LeaveRequestDto leaveRequestDto) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return leaveRequestService.createLeaveRequest(authenticatedUser, leaveRequestDto);
    }
    // 대기 휴가 신청 목록 조회
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "대기 휴가 조회", description = "승인 권한자(대표, 관리자)가 회사의 대기 중인 휴가 신청을 조회합니다.")
    public List<LeaveRequestResponseDto> getPendingRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return leaveRequestService.getPendingRequests(authenticatedUser);
    }
    // 내 휴가 신청 목록 조회
    @GetMapping("/my")
    @Operation(summary = "내 휴가 조회", description = "로그인한 사용자의 휴가 신청 내역을 확인합니다.")
    public List<LeaveRequestResponseDto> getMyRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return leaveRequestService.getMyRequests(authenticatedUser);
    }
    // 휴가 신청 승인/거절
    @PatchMapping("/update-status")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "휴가 승인/거절", description = "승인 권한자가 휴가 신청을 승인하거나 거절합니다.")
    public LeaveRequestResponseDto updateStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestBody LeaveStatusUpdateDto dto) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return leaveRequestService.updateStatus(authenticatedUser, dto);
    }

}
