package com.ji.ess.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ProfileImageResourceConfig implements WebMvcConfigurer {

    private final Path profileImageBasePath;
    private final String profileImageUrlPrefix;

    public ProfileImageResourceConfig(
            @Value("${profile.image.base-path:/var/ess/profile-images}") String profileImageBasePath,
            @Value("${profile.image.url-prefix:/profile-images}") String profileImageUrlPrefix) {
        this.profileImageBasePath = Paths.get(profileImageBasePath).toAbsolutePath().normalize();
        this.profileImageUrlPrefix = profileImageUrlPrefix.startsWith("/")
                ? profileImageUrlPrefix
                : "/" + profileImageUrlPrefix;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String handlerPattern = profileImageUrlPrefix.endsWith("/**")
                ? profileImageUrlPrefix
                : profileImageUrlPrefix + "/**";
        String resourceLocation = profileImageBasePath.toUri().toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation = resourceLocation + "/";
        }
        registry.addResourceHandler(handlerPattern)
                .addResourceLocations(resourceLocation);
    }
}

