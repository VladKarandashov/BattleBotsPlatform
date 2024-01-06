package ru.abradox.platformgateway.filters;

import net.minidev.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AddUserHeaderFilter extends AbstractGatewayFilterFactory<AddUserHeaderFilter.Config> {

    public AddUserHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> exchange.getPrincipal()
                .flatMap(authentication -> {
                    var request = exchange.getRequest();
                    var httpHeaders = HttpHeaders.writableHttpHeaders(request.getHeaders());
                    var user = ((OAuth2AuthenticationToken) authentication).getPrincipal();
                    var attributes = user.getAttributes();

                    httpHeaders.add("user", new JSONObject(attributes).toString());
                    return chain.filter(exchange);
                });
    }

    public static class Config {
        // Здесь можно добавить конфигурационные параметры, если необходимо
    }
}