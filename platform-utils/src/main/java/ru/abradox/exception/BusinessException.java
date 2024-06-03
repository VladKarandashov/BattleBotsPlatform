package ru.abradox.exception;

import lombok.Getter;
import ru.abradox.platformapi.common.response.GenericResponse;

@Getter
public class BusinessException extends RuntimeException {

    private final int statusCode;

    public BusinessException(ExceptionStatus status) {
        super(status.getMessage());
        this.statusCode = status.getCode();
    }

    public BusinessException(ExceptionStatus status, String message) {
        super(message);
        this.statusCode = status.getCode();
    }

    public BusinessException(GenericResponse<?> response) {
        super(response.getMessage());
        this.statusCode = response.getStatusCode();
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "statusCode=" + getStatusCode() +
                "message=" + getMessage() +
                '}';
    }
}
