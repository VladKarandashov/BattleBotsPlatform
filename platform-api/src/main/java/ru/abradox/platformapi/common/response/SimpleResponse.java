package ru.abradox.platformapi.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SimpleResponse {

    private int statusCode;

    private String message;

    public SimpleResponse() {
        this.statusCode = 0;
    }

    public SimpleResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public SimpleResponse(int statusCode) {
        this.statusCode = statusCode;
    }
}
