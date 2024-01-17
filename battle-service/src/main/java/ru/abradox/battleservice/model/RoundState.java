package ru.abradox.battleservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.abradox.platformapi.game.TypeRound;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("round_state")
public class RoundState {

    @Id
    private UUID id;

    private TypeRound type;

    private UUID topBotToken;

    private UUID downBotToken;
}