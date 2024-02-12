package ru.abradox.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.abradox.exception.BusinessException;
import ru.abradox.platformapi.common.response.GenericResponse;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<GenericResponse<?>> handle(BusinessException e) {
        log.error("Возникло бизнес-исключение: ", e);
        var response = new GenericResponse<>(e.getStatusCode(), e.getMessage());
        return ResponseEntity.ok()
                .header("error", Boolean.TRUE.toString())
                .body(response);
    }
}
