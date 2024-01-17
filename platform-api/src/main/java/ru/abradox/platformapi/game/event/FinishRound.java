package ru.abradox.platformapi.game.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.game.StatusRound;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinishRound {

    private UUID id;

    private StatusRound status;
}