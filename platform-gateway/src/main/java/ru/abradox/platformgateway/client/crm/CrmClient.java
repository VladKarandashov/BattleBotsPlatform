package ru.abradox.platformgateway.client.crm;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.abradox.dto.UserInfo;
import ru.abradox.exception.BusinessRedirectException;

@Component
public class CrmClient {

    private final WebClient webClient = WebClient.create();

    public Mono<UserInfo> getUserById(String id) {
        return webClient.get()
                .uri("http://localhost:8082/api/v1/auth/user/{id}", id)
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