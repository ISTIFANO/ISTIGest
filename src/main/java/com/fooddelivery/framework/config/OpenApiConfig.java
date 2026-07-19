package com.fooddelivery.framework.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foodDeliveryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ISTIGest — Food Delivery API")
                        .description("Food delivery system built with Clean Architecture (Spring Boot).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ISTIFANO")
                                .url("https://github.com/ISTIFANO/ISTIGest")));
    }
}
