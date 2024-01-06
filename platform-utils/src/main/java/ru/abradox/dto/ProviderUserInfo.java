package ru.abradox.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
}
