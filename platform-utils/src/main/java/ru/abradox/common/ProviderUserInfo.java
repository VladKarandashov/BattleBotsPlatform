package ru.abradox.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.yaml.snakeyaml.util.UriEncoder;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderUserInfo {

    @JsonProperty("id")
    private String providerId;

    @JsonProperty("login")
    private String login;

    @JsonProperty("default_email")
    private String email;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("real_name")
    private String fullName;

    @SneakyThrows
    public static ProviderUserInfo parseProviderUserInfo(String providerUserInfoEncodedJson) {
        var providerUserInfoJson = UriEncoder.decode(providerUserInfoEncodedJson);
        return new ObjectMapper().readValue(providerUserInfoJson, ProviderUserInfo.class);
    }
}
