package ru.abradox.crmservice.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "redirect")
public class RedirectProperties {

    private String lkUrl;

    private String registrationUrl;

    private String blockedUrl;
}
