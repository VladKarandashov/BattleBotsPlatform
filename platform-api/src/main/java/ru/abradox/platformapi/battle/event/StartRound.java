package ru.abradox.platformapi.battle.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.battle.TypeRound;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartRound {

    private UUID id;

    private TypeRound type;

    private UUID topBotToken;

    private UUID downBotToken;

    public StartRound(StartRound startRound) {
        this.id = startRound.id;
        this.type = startRound.type;
        this.topBotToken = startRound.topBotToken;
        this.downBotToken = startRound.downBotToken;
    }
}
