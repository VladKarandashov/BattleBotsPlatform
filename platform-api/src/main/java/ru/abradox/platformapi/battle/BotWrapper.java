package ru.abradox.platformapi.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotWrapper<T> {

    private UUID token;

    private T data;
}
