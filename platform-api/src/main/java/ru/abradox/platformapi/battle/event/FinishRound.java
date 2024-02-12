package ru.abradox.platformapi.battle.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.battle.ResultRound;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinishRound {

    private UUID id;

    private ResultRound result;
}
