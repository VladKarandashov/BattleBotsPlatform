package ru.abradox.crmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.abradox.crmservice.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Проверяет возможность входа пользователя на платформу
     *
     * @param userId id пользователя
     * @return http-ответ без тела с кодом 2** (если допустимо) или 3** (если требуются доп действия)
     */
    @GetMapping("/api/v1/user/{id}/check")
    public ResponseEntity<Void> checkUserAuth(@PathVariable("id") String userId) {
        log.info("Пришёл запрос на проверку регистрации пользователя {}", userId);
        return userService.checkUserAuth(userId);
    }
}
