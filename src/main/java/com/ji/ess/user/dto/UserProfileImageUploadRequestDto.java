package com.ji.ess.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로필 이미지 업로드 요청(multipart/form-data)")
public class UserProfileImageUploadRequestDto {
    @Schema(description = "프로필 이미지 파일", type = "string", format = "binary")
    private MultipartFile image;
}
// 프로필 이미지 업로드 요청 DTO
