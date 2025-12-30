package com.ji.ess.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ESS API",
                description = """
                        Employee Self-Service API 문서

                        모든 API 명세 확인 및 실제 요청 테스트가 가능합니다.

                        ### 테스트 계정
                        - CEO: `ceo1` / `pass1234!`
                        - EMPLOYEE: `emp1` / `pass1234!`
                        """,
                version = "v1.0",
                contact = @Contact(name = "ESS", email = "support@ess.local")
        ),
        security = {@SecurityRequirement(name = "BearerAuth")}
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
