package ru.abradox.platformgateway.client.crm;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.abradox.platformapi.common.response.GenericResponse;
import ru.abradox.platformapi.crm.UserInfo;

import static ru.abradox.platformgateway.config.CacheConfig.USER_INFO_CACHE;

@Component
@RequiredArgsConstructor
public class CrmClient {

    private final WebClient webClient = WebClient.create();

    private final LoadBalancerClient loadBalancerClient;

    @Cacheable(USER_INFO_CACHE)
    public Mono<GenericResponse<UserInfo>> getUserById(String id) {
        var uri = loadBalancerClient.choose("crm-service").getUri();
        return webClient.get()
                .uri(uri + "/internal/api/v1/auth/user/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}