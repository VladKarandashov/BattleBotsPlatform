package ru.abradox.platformapi.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinishRound {

    private UUID id;

    private StatusRound status;
}
