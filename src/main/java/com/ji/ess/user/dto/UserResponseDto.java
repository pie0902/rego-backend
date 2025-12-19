package com.ji.ess.user.dto;

import com.ji.ess.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor
public class UserResponseDto {

    private Long id;
    private String loginId;
    private String username;
    private String email;
    private String phone;
    private String role;
    private String department;
    private String position;
    private String status;
    private String address;
    private String companyName;
    private Long companyId;
    private Date createdAt;
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
