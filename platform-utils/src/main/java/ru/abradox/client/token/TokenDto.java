package ru.abradox.client.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    private String id;

    private Integer userId;

    private String title;

    private TypeToken type;

    private boolean blocked;

    public UUID getUid() {
        return UUID.fromString(id);
    }
}
