package ru.abradox.middlewareservice.service;

import ru.abradox.client.token.TokenDto;
import ru.abradox.common.UserInfo;
import ru.abradox.common.response.SimpleResponse;
import ru.abradox.middlewareservice.dto.request.CreateTokenRequest;

import java.util.List;

public interface TokenService {

    List<TokenDto> getToken(Integer userId);

    SimpleResponse createToken(UserInfo userInfo, CreateTokenRequest request);
}
