package ru.abradox.middlewareservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abradox.client.token.TokenDto;
import ru.abradox.client.token.TokenServiceClient;

import ru.abradox.common.UserInfo;
import ru.abradox.common.response.SimpleResponse;
import ru.abradox.middlewareservice.dto.request.CreateTokenRequest;
import ru.abradox.middlewareservice.service.TokenService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenServiceClient tokenServiceClient;

    @Override
    // TODO добавить кеш (желательно с очищением после createToken)
    public List<TokenDto> getToken(Integer userId) {
        return tokenServiceClient.getTokenByUser(userId);
    }

    @Override
    public SimpleResponse createToken(UserInfo userInfo, CreateTokenRequest request) {
        return tokenServiceClient.createToken(userInfo.getId(), request.getTitle(), request.getTypeToken());
    }
}
