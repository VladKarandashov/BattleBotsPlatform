package ru.abradox.middlewareservice.service;

import ru.abradox.common.response.SimpleResponse;
import ru.abradox.middlewareservice.dto.request.CreateTokenRequest;
import ru.abradox.platformapi.token.TokenDto;

import java.util.List;

public interface TokenService {

    List<TokenDto> getToken(Integer userId);

    SimpleResponse createToken(Integer userId, CreateTokenRequest request);
}
