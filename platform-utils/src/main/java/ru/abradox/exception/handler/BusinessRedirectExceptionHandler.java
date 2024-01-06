package ru.abradox.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.abradox.exception.BusinessRedirectException;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BusinessRedirectExceptionHandler {

    @ExceptionHandler(BusinessRedirectException.class)
    ResponseEntity<Void> handle(BusinessRedirectException e) {
        log.error("Возникло бизнес-исключение-редирект: ", e);
        var httpCode = e.getHttpCode() == null ? 300 : e.getHttpCode();
        return ResponseEntity
                .status(HttpStatusCode.valueOf(httpCode))
                .location(e.getLocation())
                .build();
    }
}
