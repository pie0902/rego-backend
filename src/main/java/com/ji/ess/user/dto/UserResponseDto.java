package com.ji.ess.user.dto;

import com.ji.ess.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor
@Schema(description = "사용자 응답")
public class UserResponseDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    @Schema(description = "로그인 ID", example = "ceo1")
    private String loginId;
    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;
    @Schema(description = "이메일", example = "user@thunderdev.site")
    private String email;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    @Schema(description = "역할", example = "CEO")
    private String role;
    @Schema(description = "부서", example = "개발")
    private String department;
    @Schema(description = "직급", example = "사원")
    private String position;
    @Schema(description = "가입 상태", example = "ACTIVE")
    private String status;
    @Schema(description = "주소", example = "Seoul, Korea")
    private String address;
    @Schema(description = "회사명", example = "ThunderDev")
    private String companyName;
    @Schema(description = "회사 ID", example = "1")
    private Long companyId;
    @Schema(description = "생성일시", type = "string", format = "date-time", example = "2025-01-01T09:00:00")
    private Date createdAt;
    @Schema(description = "프로필 이미지 URL", example = "/profile-images/default/default-profile.png")
    private String profileImageUrl;

    public UserResponseDto(User user, String profileImageUrl) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole().name();
        this.department = user.getDepartment();
        this.position = user.getPosition();
        this.status = user.getActive().name();
        this.address = user.getAddress();
        if (user.getCompany() != null) {
            this.companyName = user.getCompany().getCompanyName();
            this.companyId = user.getCompany().getId();
        }
        this.createdAt = user.getCreatedAt();
        this.profileImageUrl = profileImageUrl;
    }


}
// 사용자 응답 DTO
