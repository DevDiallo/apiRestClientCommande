package com.tpspringboot.apirestclientcommande.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Rest Client Commande")
                        .description("Contrat API backend pour le frontend (catalogue, commandes, admin)")
                        .version("v1")
                        .contact(new Contact().name("Equipe Backend")));
    }
}
