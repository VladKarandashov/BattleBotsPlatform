package ru.abradox.battleservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.abradox.platformapi.game.TypeRound;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "round_state", timeToLive = 60*60L)
public class RoundState {

    @Id
    private UUID id;

    private TypeRound type;

    private UUID topBotToken;

    private UUID downBotToken;
}