package ru.abradox.middlewareservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.abradox.client.token.TokenServiceClient;

import ru.abradox.middlewareservice.dto.request.CreateTokenRequest;
import ru.abradox.middlewareservice.service.TokenService;
import ru.abradox.platformapi.common.response.SimpleResponse;
import ru.abradox.platformapi.token.TokenDto;

import java.util.List;

import static ru.abradox.middlewareservice.config.CacheConfig.TOKEN_INFO_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenServiceClient tokenServiceClient;

    @Override
    @Cacheable(value = TOKEN_INFO_CACHE, key = "#userId")
    public List<TokenDto> getToken(Integer userId) {
        return tokenServiceClient.getTokenByUser(userId);
    }

    @Override
    @CacheEvict(value = TOKEN_INFO_CACHE, key = "#userId")
    public SimpleResponse createToken(Integer userId, CreateTokenRequest request) {
        return tokenServiceClient.createToken(userId, request.getTitle(), request.getTypeToken());
    }
}
