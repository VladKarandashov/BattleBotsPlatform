package ru.abradox.battlegateway.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.abradox.battlegateway.client.token.TokenClient;
import ru.abradox.client.token.TokenDto;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TokenHolder {

    private final TokenClient tokenClient;

    private final Map<UUID, TokenDto> tokens = new ConcurrentHashMap<>();

    public TokenHolder(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    public boolean isTokenExist(String botName, UUID botToken) {
        return tokens.containsKey(botToken) && botName.equalsIgnoreCase(tokens.get(botToken).getTitle());
    }

    public boolean isTokenExist(UUID botToken) {
        return tokens.containsKey(botToken);
    }

    public Optional<TokenDto> getTokenInfo(UUID botToken) {
        return Optional.ofNullable(tokens.get(botToken));
    }

    @Async
    @Scheduled(fixedRate = 2 * 1000)
    protected void updateTokens() {
        tokenClient.getNotBlockedTokens()
                .doOnNext(responseTokens -> {
                    var responseTokensMap = responseTokens.stream().collect(Collectors.toMap(
                            token -> UUID.fromString(token.getId()),
                            token -> token,
                            (v1, v2) -> v2
                    ));
                    tokens.keySet().removeIf(id -> !responseTokensMap.containsKey(id));
                    tokens.putAll(responseTokensMap);
                })
                .subscribe();
    }
}
