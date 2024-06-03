package ru.abradox.tokenservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "token")
public class TokenProperties {

    private final Integer allowedProdNumberOfTokensByUser = 1;

    private final Integer allowedDevNumberOfTokensByUser = 2;

}