package com.ji.ess.user.service;

import com.ji.ess.company.entity.Company;
import com.ji.ess.company.repository.CompanyRepository;
import com.ji.ess.company.service.CompanyService;
import com.ji.ess.user.dto.*;
import com.ji.ess.user.entity.User;
import com.ji.ess.user.entity.UserRole;
import com.ji.ess.user.entity.UserStatus;
import com.ji.ess.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

// 사용자 도메인 서비스
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;
    private final Path profileImageBasePath;
    private final String profileImageUrlPrefix;
    private final String defaultProfileImageRelativePath;

    public UserService(UserRepository userRepository,
                       CompanyRepository companyRepository,
                       CompanyService companyService,
                       PasswordEncoder passwordEncoder,
                       @Value("${profile.image.base-path:/var/ess/profile-images}") String profileImageBasePath,
                       @Value("${profile.image.url-prefix:/profile-images}") String profileImageUrlPrefix,
                       @Value("${profile.image.default-path:default/default-profile.png}") String defaultProfileImageRelativePath) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyService = companyService;
        this.passwordEncoder = passwordEncoder;
        this.profileImageBasePath = Paths.get(profileImageBasePath).toAbsolutePath().normalize();
        this.profileImageUrlPrefix = profileImageUrlPrefix.endsWith("/")
                ? profileImageUrlPrefix.substring(0, profileImageUrlPrefix.length() - 1)
                : profileImageUrlPrefix;
        this.defaultProfileImageRelativePath = defaultProfileImageRelativePath.startsWith("/")
                ? defaultProfileImageRelativePath.substring(1)
                : defaultProfileImageRelativePath;
    }

    // 회원 등록

    //대표 회원가입
    // 회사 생성과 CEO 사용자 등록
    @Transactional
    public User createCeo(CeoRegisterRequestDto dto) {
        Company company = companyService.createCompany(dto.getCompany());
        UserRequestDto userDto = dto.getUser().toUserRequestDto();
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = User.builder()
                .loginId(userDto.getLoginId())
                .username(userDto.getUsername())
                .password(encodedPassword)
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .role(UserRole.CEO)
                .department(userDto.getDepartment())
                .position(userDto.getPosition())
                .active(UserStatus.ACTIVE)
                .hireDate(userDto.getHireDate())
                .build();
        user.assignCompany(company);
        return userRepository.save(user);
    }
    // 사원 사용자 등록
    @Transactional
    public User createEmployee(EmployeeRequestDto dto) {
        UserRequestDto userDto = dto.toUserRequestDto();
        Company company = companyRepository.findById(userDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = User.builder()
                .loginId(userDto.getLoginId())
                .username(userDto.getUsername())
                .password(encodedPassword)
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .role(UserRole.EMPLOYEE)
                .department(userDto.getDepartment())
                .position(userDto.getPosition())
                .active(UserStatus.PENDING)
                .hireDate(userDto.getHireDate())
                .build();
        user.assignCompany(company);
        return userRepository.save(user);
    }
    // 승인 대기 사원 조회
    @Transactional(readOnly = true)
    public List<User> getPendingEmployees(Long companyId) {
        return userRepository.findByCompanyIdAndActive(companyId, UserStatus.PENDING);
    }
    // 사원 승인 처리
    @Transactional
    public User approveUser(Long approverId, Long targetUserId, UserApproveRequestDto requestDto) {
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("승인자(CEO) 없음"));
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사원 없음"));

        // 회사 일치 여부 확인
        if (!approver.getCompany().getId().equals(target.getCompany().getId())) {
            throw new IllegalArgumentException("다른 회사 사원을 승인할 수 없습니다.");
        }

        if (requestDto != null) {
            target.assignDepartmentAndPosition(requestDto.getDepartment(), requestDto.getPosition());

            if (requestDto.getRole() != null) {
                UserRole newRole = UserRole.valueOf(requestDto.getRole().toUpperCase());
                target.changeRole(newRole);
            }
        }

        target.activate();
        return target;
    }
    // 전체 사용자 조회
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 사용자 단건 조회
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // 내 정보 수정
    @Transactional
    public User updateCurrentUser(Long userId, UserUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.updateProfile(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getDepartment(),
                dto.getPosition(),
                dto.getAddress()
        );
        return user;
    }
    // 비밀번호 변경
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String encoded = passwordEncoder.encode(dto.getPassword());
        user.changePassword(encoded);
    }

    // 프로필 이미지 저장 및 경로 갱신
    @Transactional
    public UserProfileImageResponseDto uploadProfileImage(Long userId, UserProfileImageUploadRequestDto dto) {
        MultipartFile image = dto.getImage();
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String storedRelativePath = storeProfileImage(userId, image);
        user.updateProfileImagePath(storedRelativePath);
        return new UserProfileImageResponseDto(resolveProfileImageUrl(user));
    }

    // 응답 DTO 변환
    public UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(user, resolveProfileImageUrl(user));
    }

    private String resolveProfileImageUrl(User user) {
        String relativePath = user.getProfileImagePath();
        String targetPath = StringUtils.hasText(relativePath) ? relativePath : defaultProfileImageRelativePath;
        if (targetPath.startsWith("/")) {
            targetPath = targetPath.substring(1);
        }
        return profileImageUrlPrefix + "/" + targetPath.replace("\\", "/");
    }

    private String storeProfileImage(Long userId, MultipartFile image) {
        String extension = StringUtils.getFilenameExtension(Objects.requireNonNull(image.getOriginalFilename(), "파일 이름이 필요합니다."));
        String normalizedExtension = normalizeExtension(extension);
        String fileName = "profile." + normalizedExtension;
        Path userDirectory = profileImageBasePath.resolve(userId.toString()).normalize();
        try {
            Files.createDirectories(userDirectory);
            Path target = userDirectory.resolve(fileName);
            Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return profileImageBasePath.relativize(target).toString().replace("\\", "/");
        } catch (IOException e) {
            throw new IllegalStateException("프로필 이미지를 저장할 수 없습니다.", e);
        }
    }

    private String normalizeExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            return "png";
        }
        String lowerExt = extension.toLowerCase();
        return switch (lowerExt) {
            case "jpg", "jpeg" -> "jpg";
            case "png" -> "png";
            default -> throw new IllegalArgumentException("허용되지 않는 이미지 형식입니다.");
        };
    }
}
