package ru.abradox.middlewareservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abradox.platformapi.common.response.GenericResponse;
import ru.abradox.platformapi.crm.UserInfo;

@Slf4j
@RestController
public class UserController {

    @GetMapping(value = "/api/v1/whoami")
    public GenericResponse<UserInfo> whoami(UserInfo userInfo) {
        // Запрос от пользователя был обогащён пользовательской информацией на уровне platform-gateway.
        // Мы просто возвращаем полученную их хедера информацию пользователю
        return new GenericResponse<>(userInfo);
    }
}