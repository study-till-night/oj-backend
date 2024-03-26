package com.shuking.ojbackend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("伙伴匹配系统")
                        .description("SpringFox-SpringDoc 伙伴匹配系统 API 演示")
                        .version("1.0")
                        //  项目证书
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://gitee.com/heavyHead/Mercury/blob/master/LICENSE"))
                        //  联系方式
                        .contact(new Contact()
                                .name("shu-king")
                                .email("2421624039@qq.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("伙伴匹配系统API文档")
                        .url("/"));
    }
}
