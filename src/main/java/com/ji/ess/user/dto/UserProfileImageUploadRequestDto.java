package com.ji.ess.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileImageUploadRequestDto {
    private MultipartFile image;
}
// 프로필 이미지 업로드 요청 DTO
