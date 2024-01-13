package ru.abradox.platformgateway.client.crm;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.abradox.common.UserInfo;
import ru.abradox.exception.BusinessRedirectException;

import static ru.abradox.platformgateway.config.CacheConfig.USER_INFO_CACHE;

@Component
@RequiredArgsConstructor
public class CrmClient {

    private final WebClient webClient = WebClient.create();

    private final LoadBalancerClient loadBalancerClient;

    @Cacheable(USER_INFO_CACHE)
    public Mono<UserInfo> getUserById(String id) {
        var uri = loadBalancerClient.choose("crm-service").getUri();
        return webClient.get()
                .uri(uri + "/api/v1/auth/user/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse ->
                        clientResponse
                                .toEntity(String.class)
                                .map(entity -> new BusinessRedirectException(entity.getHeaders().getLocation(), 307))
                )
                .bodyToMono(UserInfo.class)
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().is3xxRedirection()) {
                        return new BusinessRedirectException(ex.getHeaders().getLocation(), 307);
                    }
                    return ex;
                });
    }
}