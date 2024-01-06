package ru.abradox.exception;

import lombok.Getter;
import lombok.SneakyThrows;

import java.net.URI;

@Getter
public class BusinessRedirectException extends RuntimeException {

    private final URI location;

    private Integer httpCode = null;

    public BusinessRedirectException(URI location) {
        this.location = location;
    }

    @SneakyThrows
    public BusinessRedirectException(String location) {
        this.location = new URI(location);
    }

    public BusinessRedirectException(URI location, int httpCode) {
        this.location = location;
        this.httpCode = httpCode;
    }

    @SneakyThrows
    public BusinessRedirectException(String location, int httpCode) {
        this.location = new URI(location);
        this.httpCode = httpCode;
    }
}
