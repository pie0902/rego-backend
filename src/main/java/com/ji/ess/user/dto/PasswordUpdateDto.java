package com.ji.ess.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청")
public class PasswordUpdateDto {
    @Schema(description = "변경할 비밀번호", example = "newPass1234!")
    String password;
}
// 비밀번호 변경 요청 DTO
