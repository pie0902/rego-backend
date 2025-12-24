package com.ji.ess.user.controller;

import com.ji.ess.auth.security.CustomUserDetails;
import com.ji.ess.user.dto.*;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 사용자 관리 API 컨트롤러
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "회원 가입 및 승인 API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CEO 회원가입 및 회사 생성
    @PostMapping("/ceo")
    @Operation(summary = "CEO 회원가입", description = "신규 회사와 대표 계정을 동시에 등록합니다.")
    public UserResponseDto createCeo(@RequestBody CeoRegisterRequestDto dto) {
        User savedUser = userService.createCeo(dto);
        return userService.toResponseDto(savedUser);
    }

    // 사원 회원가입
    @PostMapping("/employee")
    @Operation(summary = "사원 회원가입", description = "기존 회사에 소속된 사원을 등록합니다.")
    public UserResponseDto createEmployee(@RequestBody EmployeeRequestDto dto) {
        User savedUser = userService.createEmployee(dto);
        return userService.toResponseDto(savedUser);
    }

    // 승인 대기 사원 목록 조회
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "승인 대기 사원 목록", description = "대표가 자신의 회사에 소속된 승인 대기 사원을 조회합니다.")
    public List<UserResponseDto> getPendingEmployees(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long companyId = userDetails.getUser().getCompany().getId();
        return userService.getPendingEmployees(companyId)
                .stream()
                .map(userService::toResponseDto)
                .toList();
    }

    // 사원 승인 처리
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "사원 승인", description = "대표가 자신의 회사 소속 사원을 승인합니다.")
    public UserResponseDto approveUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody UserApproveRequestDto requestDto) {
        Long approverId = userDetails.getId(); // JWT에서 추출
        User approvedUser = userService.approveUser(approverId, id, requestDto);
        return userService.toResponseDto(approvedUser);
    }

    // 전체 사용자 목록 조회
    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAnyRole('CEO','MANAGER')")
    @Operation(summary = "사용자 목록", description = "등록된 모든 사용자를 조회합니다.")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(userService::toResponseDto)
                .toList();
    }

    // 내 정보 조회
    @GetMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    public UserResponseDto getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        User user = userService.getUserById(userId);
        return userService.toResponseDto(user);
    }

    // 내 정보 수정
    @PutMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    public UserResponseDto updateCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody UserUpdateRequestDto dto) {
        Long userId = userDetails.getId();
        User updatedUser = userService.updateCurrentUser(userId, dto);
        return userService.toResponseDto(updatedUser);
    }

    // 비밀번호 변경
    @PostMapping("/updatePassword")
    public void updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @RequestBody PasswordUpdateDto dto)
    {
        Long userId = userDetails.getUser().getId();
        userService.updatePassword(userId, dto);
    }

    // 프로필 이미지 업로드
    @PostMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 업로드", description = "현재 로그인한 사용자의 프로필 이미지를 업로드합니다.")
    public UserProfileImageResponseDto uploadProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @PathVariable Long id,
                                                          @ModelAttribute UserProfileImageUploadRequestDto requestDto) {
        validateSameUser(userDetails, id);
        return userService.uploadProfileImage(id, requestDto);
    }

    private void validateSameUser(CustomUserDetails userDetails, Long targetUserId) {
        if (userDetails == null || !userDetails.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("본인만 프로필 이미지를 변경할 수 있습니다.");
        }
    }
}
