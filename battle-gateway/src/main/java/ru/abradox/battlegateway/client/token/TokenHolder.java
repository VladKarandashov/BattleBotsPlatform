package ru.abradox.battlegateway.client.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
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

    public boolean isNotTokenExist(String botName, UUID botToken) {
        return !isTokenExist(botName, botToken);
    }

    public boolean isTokenExist(UUID botToken) {
        return tokens.containsKey(botToken);
    }

    public boolean isNotTokenExist(UUID botToken) {
        return !isTokenExist(botToken);
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
                    log.info("Успешно получены активные токены [{}]", tokens.keySet());
                })
                .subscribe();
    }
}
