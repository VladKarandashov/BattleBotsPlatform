package ru.abradox.middlewareservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.token.TypeToken;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTokenRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Zа-яА-Я0-9]{1,16}$")
    private String title;

    @NotNull
    private TypeToken typeToken;
}