package ru.abradox.tokenservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.abradox.client.token.request.CreateTokenRequest;
import ru.abradox.dto.response.SimpleResponse;
import ru.abradox.client.token.TokenDto;
import ru.abradox.tokenservice.service.TokenService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/api/v1/token")
    public List<TokenDto> getToken() {
        log.info("Начинаю обрабатывать запрос на получение всех токенов");
        var tokens = tokenService.getToken();
        log.info("Успешно получил {} токенов", tokens.size());
        return tokens;
    }

    @GetMapping("/api/v1/token/byUuid/{uuid}")
    public TokenDto getToken(@PathVariable("uuid") UUID tokenId) {
        log.info("Начинаю обрабатывать запрос на получение токена {}", tokenId);
        var token = tokenService.getToken(tokenId);
        log.info("Успешно получил информацию по токену {}", token);
        return token;
    }

    @GetMapping("/api/v1/token/byUser/{id}")
    public List<TokenDto> getToken(@PathVariable("id") Integer userId) {
        log.info("Начинаю обрабатывать запрос на получение токена {}", userId);
        var token = tokenService.getToken(userId);
        log.info("Успешно получил информацию по токену {}", token);
        return token;
    }

    @GetMapping("/api/v1/token/byTitle/{title}")
    public List<TokenDto> getToken(@PathVariable("title") String title) {
        log.info("Начинаю обрабатывать запрос на получение токена для бота {}", title);
        var token = tokenService.getToken(title);
        log.info("Успешно получил информацию по токену {}", token);
        return token;
    }

    @PostMapping("/api/v1/token")
    public SimpleResponse createToken(@RequestBody CreateTokenRequest request)
    {
        log.info("Начинаю процедуру создания токена по запросу {}", request);
        tokenService.createToken(request);
        log.info("Токены успешно созданы");
        return new SimpleResponse();
    }
}
