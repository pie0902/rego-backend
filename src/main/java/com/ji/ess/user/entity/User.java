package com.ji.ess.user.entity;

import com.ji.ess.company.entity.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
// 사용자 엔티티
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String loginId; // 로그인용 고유 ID
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    private String phone;
    private String address;
    @Column(name = "profile_image_path")
    private String profileImagePath;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    private String department; // 부서명
    private String position; // 직급
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus active; // 재직 여부
    private LocalDate hireDate;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;


    // 필수값으로 사용자 생성
    @Builder
    private User(String loginId,
                 String username,
                 String password,
                 String email,
                 String phone,
                 String address,
                 UserRole role,
                 String department,
                 String position,
                 UserStatus active,
                 LocalDate hireDate) {
        this.loginId = loginId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.department = StringUtils.hasText(department) ? department : null;
        this.position = StringUtils.hasText(position) ? position : null;

        this.active = active;
        this.hireDate = (hireDate != null) ? hireDate : LocalDate.now();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    // 회사 배정
    public void assignCompany(Company company) {
        this.company = company;
    }
    // 재직 상태로 변경
    public void activate() {
        this.active = UserStatus.ACTIVE;
    }
    // 퇴사 상태로 변경
    public void deactivate() {
        this.active = UserStatus.INACTIVE;
    }

    // 부서와 직책 변경
    public void assignDepartmentAndPosition(String department, String position) {
        String normalizedDepartment = StringUtils.hasText(department) ? department : null;
        String normalizedPosition = StringUtils.hasText(position) ? position : null;

        boolean updated = false;
        if (!Objects.equals(this.department, normalizedDepartment)) {
            this.department = normalizedDepartment;
            updated = true;
        }
        if (!Objects.equals(this.position, normalizedPosition)) {
            this.position = normalizedPosition;
            updated = true;
        }
        if (updated) {
            this.updatedAt = new Date();
        }
    }

    // 역할 변경
    public void changeRole(UserRole newRole) {
        if (newRole == null || this.role == newRole) {
            return;
        }
        this.role = newRole;
        this.updatedAt = new Date();
    }

    // 프로필 정보 수정
    public void updateProfile(String username,
                              String email,
                              String phone,
                              String department,
                              String position,
                              String address) {
        boolean updated = false;

        if (username != null) {
            this.username = username;
            updated = true;
        }
        if (email != null) {
            this.email = email;
            updated = true;
        }
        if (phone != null) {
            this.phone = phone;
            updated = true;
        }
        if (department != null) {
            this.department = department;
            updated = true;
        }
        if (position != null) {
            this.position = position;
            updated = true;
        }
        if (address != null) {
            this.address = address;
            updated = true;
        }

        if (updated) {
            this.updatedAt = new Date();
        }
    }
    // 비밀번호 변경
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 프로필 이미지 경로 갱신
    public void updateProfileImagePath(String newProfileImagePath) {
        if (Objects.equals(this.profileImagePath, newProfileImagePath)) {
            return;
        }
        this.profileImagePath = newProfileImagePath;
        this.updatedAt = new Date();
    }
}
