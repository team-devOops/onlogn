package com.onlogn.onlogn.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("O(NlogN) API v1")
                        .version("1.0.0-draft")
                        .description("O(NlogN) API 계약 스켈레톤.\n이 기준선은 루트 메타데이터, 공통 보안, 재사용 가능한 컴포넌트를 정의한다.")
                        .contact(new Contact().name("O(NlogN) API Team").url("https://api.onlogn.com")))
                .addServersItem(new Server().url("https://api.onlogn.com").description("운영 환경"))
                .addServersItem(new Server().url("http://localhost:3000").description("로컬 개발"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("보호 엔드포인트를 위한 access token 전달 방식.\nauth 엔드포인트에서는 토큰을 JSON 응답 body로 전달한다.")))
                .tags(List.of(
                        new Tag().name("auth").description("인증 및 세션 작업."),
                        new Tag().name("users").description("인증 사용자 프로필 작업."),
                        new Tag().name("profiles").description("공개 프로필 및 프로필 범위 조회 작업."),
                        new Tag().name("groups").description("소유자 범위 group 관리 작업."),
                        new Tag().name("tasks").description("Task CRUD 및 조회 작업."),
                        new Tag().name("calendar").description("캘린더 집계 작업."),
                        new Tag().name("reactions").description("공개 task reaction 작업.")));
    }
}
