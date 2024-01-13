package ru.abradox.client.token.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.client.token.TypeToken;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTokenPairRequest {

    private Integer userId;

    private String title;

    private TypeToken typeToken;
}
