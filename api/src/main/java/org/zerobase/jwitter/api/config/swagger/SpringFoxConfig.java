package org.zerobase.jwitter.api.config.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.zerobase.jwitter.api.config.swagger.plugin.EmailAnnotationPlugin;
import org.zerobase.jwitter.domain.model.Follow;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.List;

@EnableSwagger2
@Import({BeanValidatorPluginsConfiguration.class, SpringDataRestConfiguration.class})
@Configuration
public class SpringFoxConfig {

    @Bean
    public Docket api(TypeResolver typeResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .consumes(new HashSet<>(List.of("application/json")))
                .produces(new HashSet<>(List.of("application/json")))
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.zerobase.jwitter.api"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo())
                .ignoredParameterTypes(Follow.Id.class)
                .additionalModels(typeResolver.resolve(Follow.class))
                .useDefaultResponseMessages(false)
                .genericModelSubstitutes(ResponseEntity.class)
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(securityScheme()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Jwitter timeline REST API")
                .description("Java implementation of Twitter's timeline service")
                .version("1.0")
                .contact(new Contact("Jwitter",
                        "https://cuboid-tarantula-e0b.notion.site/Jwitter-e1968f7cf2f24369a9832103a7e44816",
                        "sinclairjang@gmail.com"))
                .build();
    }

    @Bean
    public EmailAnnotationPlugin emailPlugin() {
        return new EmailAnnotationPlugin();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(securityReferences())
                .operationSelector(operationContext -> true)
                .build();
    }

    private List<SecurityReference> securityReferences() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = new AuthorizationScope("global", "accessEverything");
        return List.of(new SecurityReference("Authorization", authorizationScopes));
    }

    private ApiKey securityScheme() {
        String targetHeader = "Authorization";
        return new ApiKey("Authorization", targetHeader, "header");
    }
}
