package ru.abradox.platformapi.cardgame.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.cardgame.RoundStateInfo;

@Data
@NoArgsConstructor
public class ServerResponse {

    private int statusCode;

    private String message;

    private RoundStateInfo data;

    public ServerResponse(StatusCode statusCode, RoundStateInfo data) {
        this.statusCode = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.data = data;
    }
}
