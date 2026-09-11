package br.com.raizesdonordeste.infrastructure.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                // Informações da API
                .info(
                        new Info()
                                .title(
                                        "Rede Raízes do Nordeste - API"
                                )
                                .version("1.0")
                                .description(
                                        "API REST do Projeto Multidisciplinar "
                                        + "Rede Raízes do Nordeste"
                                )
                )

                // Configuração do JWT
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(
                                                        SecurityScheme.Type.HTTP
                                                )
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )

                // Indica que as operações da API utilizam JWT
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                );
    }
}