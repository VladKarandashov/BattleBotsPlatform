package ru.abradox.platformapi.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public UUID getUid() {
        return UUID.fromString(id);
    }
}