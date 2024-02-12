package ru.abradox.platformapi.statistic.current;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotInfo {

    private String title;

    private Integer position;

    private Boolean isActive;
}
