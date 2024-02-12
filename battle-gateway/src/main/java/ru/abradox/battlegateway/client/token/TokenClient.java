package ru.abradox.battlegateway.client.token;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.abradox.platformapi.token.TokenDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenClient {

    private final WebClient webClient = WebClient.create();

    private final LoadBalancerClient loadBalancerClient;

    public Mono<List<TokenDto>> getNotBlockedTokens() {
        var uri = loadBalancerClient.choose("token-service").getUri();
        return webClient.get()
                .uri(uri + "/api/v1/token")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}