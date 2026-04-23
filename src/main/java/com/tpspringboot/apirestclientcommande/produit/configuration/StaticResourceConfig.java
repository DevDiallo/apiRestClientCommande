package com.tpspringboot.apirestclientcommande.produit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose product images under /api/images/** so frontend proxy can serve them without 4200/images 404.
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}
