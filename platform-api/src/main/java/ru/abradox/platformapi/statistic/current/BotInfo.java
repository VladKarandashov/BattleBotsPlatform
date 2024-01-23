package ru.abradox.platformapi.statistic.current;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotInfo {

    private UUID token;

    private Integer position;

    private Boolean isActive;
}
