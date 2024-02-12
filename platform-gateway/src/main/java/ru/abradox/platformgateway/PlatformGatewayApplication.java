package ru.abradox.platformgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "ru.abradox")
@ConfigurationPropertiesScan
@EnableConfigurationProperties
public class PlatformGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlatformGatewayApplication.class, args);
	}
}
