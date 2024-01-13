package ru.abradox.tokenservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abradox.client.token.request.CreateTokenRequest;
import ru.abradox.client.token.TokenDto;
import ru.abradox.exception.BusinessException;
import ru.abradox.tokenservice.config.TokenProperties;
import ru.abradox.tokenservice.entity.TokenEntity;
import ru.abradox.tokenservice.mapper.TokenMapper;
import ru.abradox.tokenservice.repository.TokenRepository;
import ru.abradox.tokenservice.service.TokenService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    private final TokenProperties tokenProperties;

    private final TokenMapper tokenMapper;

    @Override
    public List<TokenDto> getToken() {
        return tokenMapper.mapTokenEntityToTokenDto(tokenRepository.findAllByBlockedIsFalse());
    }

    @Override
    public TokenDto getToken(UUID tokenId) {
        return tokenMapper.mapTokenEntityToTokenDto(tokenRepository.findByIdAndBlockedIsFalse(tokenId));
    }

    @Override
    public List<TokenDto> getToken(Integer userId) {
        return tokenMapper.mapTokenEntityToTokenDto(tokenRepository.findAllByUserIdAndBlockedIsFalse(userId));
    }

    @Override
    public List<TokenDto> getToken(String title) {
        return tokenMapper.mapTokenEntityToTokenDto(tokenRepository.findAllByTitleAndBlockedIsFalse(title));
    }

    @Override
    @Transactional
    public void createToken(CreateTokenRequest request) {
        var userId = request.getUserId();
        var botTitle = request.getTitle();
        var typeToken = request.getTypeToken();

        // проверяем количество уже выданных токенов для прода
        var userTokensCount = tokenRepository.countAllByUserIdAndType(userId, typeToken);
        if (userTokensCount >= tokenProperties.getAllowedProdNumberOfTokensByUser()) {
            throw new BusinessException(1000, "Максимальное количество токенов такого типа уже достигнуто");
        }

        // проверяем существование бота с таким именем
        var isTitleExist = tokenRepository.existsByTitle(botTitle);
        if (isTitleExist) {
            throw new BusinessException(1000, "Бот с таким именем уже зарегистрирован");
        }

        var token = new TokenEntity(userId, botTitle, typeToken);
        tokenRepository.save(token);
    }
}
