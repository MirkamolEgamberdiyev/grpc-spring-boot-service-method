package com.nemo.grpcexampleclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@EnableSwagger2
@Configuration
public class SwaggerConf {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.nemo.grpcexampleclient"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("example-api")
                .description("grpc 4 types of request test api")
                .termsOfServiceUrl("")
                .contact(new Contact("Mirkamol", "https://github.com/MirkamolEgamberdiyev?tab=repositories", "mirkamol9187907@gmail.com"))
                .version("1.0")
                .build();
    }
}
