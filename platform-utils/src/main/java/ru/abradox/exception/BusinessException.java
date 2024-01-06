package ru.abradox.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int statusCode;

    private final Object data;

    public BusinessException(int statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public BusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.data = null;
    }

    public BusinessException(int statusCode, String message, Object data) {
        super(message);
        this.statusCode = statusCode;
        this.data = data;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "statusCode=" + getStatusCode() +
                "message=" + getMessage() +
                ", data=" + getData() +
                '}';
    }
}
