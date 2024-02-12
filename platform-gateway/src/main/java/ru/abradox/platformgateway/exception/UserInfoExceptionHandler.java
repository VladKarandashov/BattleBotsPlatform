package ru.abradox.platformgateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.abradox.platformapi.common.response.GenericResponse;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserInfoExceptionHandler {

    @ExceptionHandler(UserInfoException.class)
    ResponseEntity<GenericResponse<?>> handle(UserInfoException e) {
        log.error("Возникло бизнес-исключение: ", e);
        var response = new GenericResponse<>(e.getStatusCode(), e.getMessage());
        return ResponseEntity.ok()
                .header("error", Boolean.TRUE.toString())
                .body(response);
    }
}