package ru.abradox.platformapi.cardgame.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    private UUID roundId;

    @Valid
    @NotNull
    private ActionCode code;

    @Size(max = 6)
    private Set<CardDto> cards;

    public BotAction(UUID roundId, ActionCode code) {
        this.roundId = roundId;
        this.code = code;
    }
}
