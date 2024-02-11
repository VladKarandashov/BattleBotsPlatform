package ru.abradox.platformgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
class SecurityConfig {

    @Bean
    public SecurityWebFilterChain clientSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .oauth2Login(Customizer.withDefaults())
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/").permitAll()
                        .pathMatchers("/healthcheck").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated())
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://147.45.107.29:8070", "http://147.45.107.29:8080"));
        configuration.setMaxAge(168000L);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        //or any domain that you want to restrict to
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "OPTIONS"));
        //Add the method support as you like
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
