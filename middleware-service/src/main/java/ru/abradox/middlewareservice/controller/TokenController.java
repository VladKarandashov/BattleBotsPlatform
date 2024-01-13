package ru.abradox.middlewareservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.abradox.client.token.TokenDto;
import ru.abradox.dto.UserInfo;
import ru.abradox.dto.response.GenericResponse;
import ru.abradox.dto.response.SimpleResponse;
import ru.abradox.middlewareservice.dto.request.CreateTokenRequest;
import ru.abradox.middlewareservice.service.TokenService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/api/v1/token")
    public GenericResponse<List<TokenDto>> getToken(UserInfo userInfo) {
        var userId = userInfo.getId();
        var tokens = tokenService.getToken(userId);
        return new GenericResponse<>(tokens);
    }

    @PostMapping("/api/v1/token")
    // TODO навесить rate limit
    public SimpleResponse createToken(UserInfo userInfo, @Valid @RequestBody CreateTokenRequest request) {
        return tokenService.createToken(userInfo, request);
    }
}
