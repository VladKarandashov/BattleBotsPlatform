package ru.abradox.battlegateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.abradox.battlegateway.client.token.TokenHolder;
import ru.abradox.battlegateway.service.AuthService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenHolder tokenHolder;

    @Override
    public boolean checkBotAccess(String botName, String botToken) {
        if (StringUtils.isBlank(botName)) return false;
        if (StringUtils.isBlank(botToken)) return false;
        return parseUUID(botToken)
                .filter(uuid -> tokenHolder.isTokenExist(botName, uuid))
                .isPresent();
    }

    private Optional<UUID> parseUUID(String str) {
        try {
            return Optional.of(UUID.fromString(str));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
