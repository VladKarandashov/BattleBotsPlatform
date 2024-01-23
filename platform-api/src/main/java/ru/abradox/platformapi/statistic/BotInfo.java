package ru.abradox.platformapi.statistic;

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
