package ru.abradox.platformgateway.view;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HtmlPageConfig {

    @Bean
    public RouterFunction<ServerResponse> htmlPageRoute() {
        return RouterFunctions.route(RequestPredicates.GET("/"), request -> {
            ClassPathResource resource = new ClassPathResource("templates/index.html");
            return ServerResponse.ok().bodyValue(resource);
        });
    }
}