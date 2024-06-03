package ru.abradox.platformgateway.exception;

import lombok.Getter;
import ru.abradox.platformapi.common.response.GenericResponse;

@Getter
public class UserInfoException extends RuntimeException {

    private final int statusCode;

    public UserInfoException(GenericResponse<?> response) {
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