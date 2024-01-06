package ru.abradox.middlewareservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
public class UserController {

    @GetMapping("/api/v1/whoami")
    public String whoami(@RequestHeader("user") String userInfo) {
        return userInfo;
    }
}