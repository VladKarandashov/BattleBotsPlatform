package ru.abradox.client.crm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrmServiceClient {

    public static final String SERVICE_NAME = "crm-service";

    private static final RestTemplate restTemplate = new RestTemplate();

    private final LoadBalancerClient loadBalancerClient;

    private URI getServiceUri() {
        return loadBalancerClient.choose(SERVICE_NAME).getUri();
    }

    public List<Integer> getBlockedUserIds() {
        String url = getServiceUri() + "/internal/api/v1/user/blocked";
        var response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Integer>>() {});
        return response.getBody();
    }
}
