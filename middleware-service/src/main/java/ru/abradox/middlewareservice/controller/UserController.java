package ru.abradox.middlewareservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abradox.dto.UserInfo;

@Slf4j
@RestController
public class UserController {

    @GetMapping(value = "/api/v1/whoami")
    public UserInfo whoami(UserInfo userInfo) {
        return userInfo;
    }

    // TODO удалить
    @PostMapping(value = "/api/v1/whoami")
    public UserInfo whoamiPost(UserInfo userInfo) {
        return userInfo;
    }
}