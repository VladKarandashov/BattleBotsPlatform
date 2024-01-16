package ru.abradox.platformapi.game.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.game.TypeRound;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartRound {

    private UUID id;

    private TypeRound type;

    private UUID topBotToken;

    private UUID downBotToken;
}
