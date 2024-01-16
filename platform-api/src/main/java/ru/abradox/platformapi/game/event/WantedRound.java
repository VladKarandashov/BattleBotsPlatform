package ru.abradox.platformapi.game.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.game.TypeRound;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WantedRound extends StartRound {

    public WantedRound(UUID id, TypeRound type, UUID topBotToken, UUID downBotToken) {
        super(id, type, topBotToken, downBotToken);
    }
}
