package ru.abradox.platformgateway.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.util.UriEncoder;
import ru.abradox.dto.UserInfo;
import ru.abradox.platformgateway.client.crm.CrmClient;

@Slf4j
@Component
public class AddUserInfoHeader extends AbstractGatewayFilterFactory<AddUserInfoHeader.Config> {

    private final CrmClient crmClient;

    public AddUserInfoHeader(CrmClient crmClient) {
        super(AddUserInfoHeader.Config.class);
        this.crmClient = crmClient;
    }

    @Override
    public GatewayFilter apply(AddUserInfoHeader.Config config) {

        return (exchange, chain) -> exchange.getPrincipal()
                .cast(OAuth2AuthenticationToken.class)
                .map(authentication -> {
                    var user = authentication.getPrincipal();
                    var attributes = user.getAttributes();
                    return attributes.get("id").toString();
                })
                .flatMap(crmClient::getUserById)
                .doOnNext(userInfo -> {
                    var request = exchange.getRequest();
                    var httpHeaders = HttpHeaders.writableHttpHeaders(request.getHeaders());
                    var userInfoJson = produceUserInfoJson(userInfo);
                    var userInfoJsonEncoded = UriEncoder.encode(userInfoJson);
                    httpHeaders.add("user", userInfoJsonEncoded);
                }).then(chain.filter(exchange));
    }

    private String produceUserInfoJson(UserInfo userInfo) {
        try {
            return new ObjectMapper().writeValueAsString(userInfo);
        } catch (Exception e) {
            log.error("Error in user info filter - parse json: ", e);
            return "n/a";
        }
    }

    public static class Config {
        // Здесь можно добавить конфигурационные параметры, если необходимо
    }
}
