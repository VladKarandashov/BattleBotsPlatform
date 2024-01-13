package ru.abradox.client.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.abradox.client.token.request.CreateTokenRequest;
import ru.abradox.common.response.SimpleResponse;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenServiceClient {

    public static final String SERVICE_NAME = "token-service";

    private static final RestTemplate restTemplate = new RestTemplate();

    private final LoadBalancerClient loadBalancerClient;

    public List<TokenDto> getTokenByUser(Integer userId) {
        String url = getServiceUri() + "/api/v1/token/byUser/{id}";
        var response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<TokenDto>>() {}, userId.toString());
        return response.getBody();
    }

    public SimpleResponse createToken(Integer userId, String titleBot, TypeToken typeToken) {
        String url = getServiceUri() + "/api/v1/token";
        var httpEntity = new HttpEntity<>(new CreateTokenRequest(userId, titleBot, typeToken));
        var response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, SimpleResponse.class);
        return response.getBody();
    }

    private URI getServiceUri() {
        return loadBalancerClient.choose(SERVICE_NAME).getUri();
    }
}
