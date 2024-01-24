package ru.abradox.client.token.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.token.TypeToken;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTokenRequest {

    private Integer userId;

    private String title;

    private TypeToken typeToken;
}
