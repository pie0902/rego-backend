package com.ji.ess.attendance.controller;

import com.ji.ess.auth.dto.AuthenticatedUserDto;
import com.ji.ess.auth.security.CustomUserDetails;
import com.ji.ess.attendance.dto.AttendanceResponseDto;
import com.ji.ess.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 근태 관리 API 컨트롤러
@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance", description = "출퇴근 및 근태 조회 API")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
    // 내 근태 기록 조회
    @GetMapping("/me")
    @Operation(summary = "내 근태 조회", description = "로그인한 사용자의 근태 기록을 확인합니다.")
    public List<AttendanceResponseDto> getMyAttendance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return attendanceService.getAttendanceByUser(authenticatedUser);
    }

    // 출근 처리
    @PostMapping("/check-in")
    @Operation(summary = "출근 체크", description = "로그인한 사용자의 출근 시간을 기록합니다.")
    public AttendanceResponseDto checkIn(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return attendanceService.checkIn(authenticatedUser);
    }
    // 퇴근 처리
    @PostMapping("/check-out")
    @Operation(summary = "퇴근 체크", description = "로그인한 사용자의 퇴근 시간을 기록합니다.")
    public AttendanceResponseDto checkOut(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto(userDetails);
        return attendanceService.checkOut(authenticatedUser);
    }
    //각종 승인 로직


}
