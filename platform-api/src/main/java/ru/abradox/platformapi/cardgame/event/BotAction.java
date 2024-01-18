package ru.abradox.platformapi.cardgame.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.cardgame.CardDto;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotAction {

    private UUID roundId;

    private ActionCode code;

    private Set<CardDto> cards;
}
