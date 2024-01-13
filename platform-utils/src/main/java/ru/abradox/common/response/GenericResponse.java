package ru.abradox.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenericResponse<T> {

    private int statusCode;

    private String message;

    private T data;

    public GenericResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public GenericResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public GenericResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public GenericResponse(T data) {
        this.statusCode = 0;
        this.data = data;
    }
}
