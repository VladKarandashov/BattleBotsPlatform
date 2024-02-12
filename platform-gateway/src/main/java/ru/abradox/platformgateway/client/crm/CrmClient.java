package ru.abradox.platformgateway.client.crm;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.abradox.platformapi.common.response.GenericResponse;
import ru.abradox.platformapi.crm.UserInfo;
import ru.abradox.platformgateway.exception.UserInfoException;

import static ru.abradox.platformgateway.config.CacheConfig.USER_INFO_CACHE;

@Component
@RequiredArgsConstructor
public class CrmClient {

    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @Cacheable(USER_INFO_CACHE)
    public Mono<GenericResponse<UserInfo>> getUserById(String id) {
        return WebClient.builder().filter(lbFunction).build().get()
                .uri("http://crm-service" + "/internal/api/v1/auth/user/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GenericResponse<UserInfo>>() {})
                .flatMap(userInfoResponse -> {
                    if (userInfoResponse.getStatusCode() != 0) {
                        return Mono.error(new UserInfoException(userInfoResponse));
                    }
                    return Mono.just(userInfoResponse);
                });
    }
}