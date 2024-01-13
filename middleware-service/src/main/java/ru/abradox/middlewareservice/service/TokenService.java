package ru.abradox.middlewareservice.service;

import ru.abradox.client.token.TokenDto;

import java.util.List;

public interface TokenService {

    List<TokenDto> getToken(Integer userId);
}
