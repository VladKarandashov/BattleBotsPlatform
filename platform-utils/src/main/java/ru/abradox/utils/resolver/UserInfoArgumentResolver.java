package ru.abradox.utils.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.yaml.snakeyaml.util.UriEncoder;
import ru.abradox.dto.UserInfo;

@Slf4j
public class UserInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return UserInfo.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws JsonProcessingException {

        var request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            log.error("request is null");
            return null;
        }

        var userInfoEncodedJson = request.getHeader("user");
        var userInfoJson = UriEncoder.decode(userInfoEncodedJson);
        var userInfo = OBJECT_MAPPER.readValue(userInfoJson, UserInfo.class);
        log.info("Получен пользователь: {}", userInfo);
        return userInfo;
    }
}
