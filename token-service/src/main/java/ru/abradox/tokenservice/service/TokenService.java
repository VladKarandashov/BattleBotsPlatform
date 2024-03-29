package ru.abradox.tokenservice.service;

import ru.abradox.client.token.request.CreateTokenRequest;
import ru.abradox.platformapi.token.TokenDto;

import java.util.List;
import java.util.UUID;

public interface TokenService {

    List<TokenDto> getToken();

    TokenDto getToken(UUID tokenId);

    List<TokenDto> getToken(Integer userId);

    List<TokenDto> getToken(String title);

    void createToken(CreateTokenRequest request);
}
