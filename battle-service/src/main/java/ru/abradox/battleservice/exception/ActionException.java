package ru.abradox.battleservice.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.abradox.platformapi.cardgame.event.ServerResponse;

@Getter
@RequiredArgsConstructor
public class ActionException extends RuntimeException {

    private final ServerResponse response;
}
