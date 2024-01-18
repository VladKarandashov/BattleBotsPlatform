package ru.abradox.platformapi.cardgame.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StatusCode {

    // 1xx - системные
    // 2xx - информирующие
    // 3xx - требующие действия
    // 4xx - ошибочные


    START_ROUND(201, "Начинается новый раунд, ход соперника"),

    START_ROUND_WITH_ACTIVE(301, "Начинается новый раунд, ваш ход"),

    INTERNAL_ERROR(400, "Произошла внезапная ошибка на сервере"),
    ROUND_NOT_FOUND(401, "Раунд не найден"),
    ROUND_ALREADY_FINISHED(402, "Раунд уже завершён"),
    NOT_YOUR_TURN(403, "Сейчас очередь другого игрока"),
    WRONG_ACTION_TYPE(404, "Неправильный тип хода (защита/атака)"),
    ;

    private final int code;

    private final String message;


    @Override
    public String toString() {
        return this.code + ": " + this.message;
    }
}
