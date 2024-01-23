package ru.abradox.client.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.abradox.platformapi.statistic.current.CompetitionInfo;
import ru.abradox.platformapi.statistic.history.HistoryInfo;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticServiceClient {

    public static final String SERVICE_NAME = "statistic-service";

    private static final RestTemplate restTemplate = new RestTemplate();

    private final LoadBalancerClient loadBalancerClient;

    public CompetitionInfo getCompetitionInfo() {
        String url = getServiceUri() + "/api/v1/competition";
        var response = restTemplate.exchange(url, HttpMethod.GET, null, CompetitionInfo.class);
        return response.getBody();
    }

    public List<HistoryInfo> getHistoryInfo() {
        String url = getServiceUri() + "/api/v1/history";
        var response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<HistoryInfo>>() {
        });
        return response.getBody();
    }

    private URI getServiceUri() {
        return loadBalancerClient.choose(SERVICE_NAME).getUri();
    }
}
