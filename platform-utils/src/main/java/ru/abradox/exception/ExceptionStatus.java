package ru.abradox.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionStatus {

    // 1xxx - ошибки авторизации
    // 2xxx - бизнес ошибки
    // 3xxx - требующие редиректа

    REGISTRATION_ERROR(1001, "Произошла ошибка при регистрации"),

    INTERNAL_ERROR(2000, "Произошла внезапная ошибка на сервере"),
    CREATE_TOKEN_ERROR(2011, "Ошибка при создании токена"),

    PLATFORM_REDIRECT(3000, "Пользователь успешно вошёл, переходим в личный кабинет"),
    REGISTRATION_REDIRECT(3001, "Требуется регистрация"),
    BLOCKED_REDIRECT(3002, "Пользователь заблокирован"),
    ;

    private final int code;

    private final String message;

    @Override
    public String toString() {
        return this.code + ": " + this.message;
    }
}
