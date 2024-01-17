package ru.abradox.platformapi.cardgame.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StatusCode {

    START_ROUND(1, "Начинается новый раунд"),
//    NOT_FOUND(404, "Not Found"),
//    INTERNAL_ERROR(500, "Internal Server Error"),
//    BAD_REQUEST(400, "Bad Request")
    ;

    private final int code;

    private final String message;


    @Override
    public String toString() {
        return this.code + ": " + this.message;
    }
}
