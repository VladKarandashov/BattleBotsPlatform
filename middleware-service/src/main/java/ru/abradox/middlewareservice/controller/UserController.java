package ru.abradox.middlewareservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.util.UriEncoder;

@Slf4j
@RestController
public class UserController {

    @GetMapping(value = "/api/v1/whoami", produces = MediaType.APPLICATION_JSON_VALUE)
    public String whoami(@RequestHeader("user") String userInfo) {
        return UriEncoder.decode(userInfo);
    }
}