package ru.abradox.crmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abradox.crmservice.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/internal/api/v1/user/blocked")
    public List<Integer> getBlockedUserIds() {
        return userService.getBlockedUserIds();
    }
}
