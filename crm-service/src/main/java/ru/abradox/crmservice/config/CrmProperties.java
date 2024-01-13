package ru.abradox.crmservice.config;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Data
@ConfigurationProperties(prefix = "crm")
public class CrmProperties {

    private String PLATFORM_LK_URI = "/middleware/view";

    @SneakyThrows
    public URI getPlatformUri() {
        return new URI(PLATFORM_LK_URI);
    }
}