package ru.abradox.crmservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteRegistrationRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Zа-яА-Я0-9]{1,32}$")
    private String nickName;
}
