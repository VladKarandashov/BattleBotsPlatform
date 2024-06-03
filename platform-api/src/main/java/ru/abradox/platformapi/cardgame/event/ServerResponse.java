package ru.abradox.platformapi.cardgame.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.abradox.platformapi.cardgame.RoundStateInfo;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServerResponse {

    private int statusCode;

    private String message;

    private RoundStateInfo data;

    public ServerResponse(StatusCode statusCode, RoundStateInfo data) {
        this.statusCode = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.data = data;
    }

    public ServerResponse(StatusCode statusCode) {
        this.statusCode = statusCode.getCode();
        this.message = statusCode.getMessage();
    }
}
