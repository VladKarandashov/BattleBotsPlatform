package ru.abradox.crmservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.abradox.crmservice.config.CrmProperties;
import ru.abradox.crmservice.dto.request.CompleteRegistrationRequest;
import ru.abradox.crmservice.service.AuthService;
import ru.abradox.platformapi.crm.UserInfo;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final CrmProperties crmProperties;

    private final AuthService authService;

    /**
     * ИСПОЛЬЗУЕТСЯ ПРИ КАЖДОМ ЗАПРОСЕ + ОБЛАДАЕТ КЭШЭМ.
     * Возвращает информацию о пользователе если вход доступен.
     * Если вход не доступен - возвращает редирект на нужную страницу
     *
     * @param providerUserId id пользователя от id-провайдера
     * @return http-ответ с userInfo и кодом 2** (если допустимо) или 3** (если требуются доп действия)
     */
    @GetMapping("/api/v1/auth/user/{id}")
    public UserInfo getUserInfo(@PathVariable("id") String providerUserId) {
        log.info("Пришёл запрос на получение информации пользователя {}", providerUserId);
        return authService.getUserInfo(providerUserId);
    }

    /**
     * Завершает процедуру входа на платформу и перенаправляет в личный кабинет
     *
     * @param providerUserInfoEncodedJson информация от id-провайдера
     * @return редирект в личный кабинет
     */
    @PostMapping("/api/v1/auth")
    public ResponseEntity<Void> authorize(@RequestHeader("user") String providerUserInfoEncodedJson) {
        log.info("Выполняю вход пользователя {}", providerUserInfoEncodedJson);
        authService.updateUserInfo(providerUserInfoEncodedJson);
        return redirectToLk();
    }

    /**
     * Окончание регистрации. Сохранение данных пользователя
     *
     * @param providerUserInfoEncodedJson информация от id-провайдера
     * @param request                     данные формы
     * @return редирект в личный кабинет
     */
    @PostMapping("/api/v1/auth/completeRegistration")
    public ResponseEntity<Void> completeRegistration(
            @RequestHeader("user") String providerUserInfoEncodedJson,
            @Valid @RequestBody CompleteRegistrationRequest request) {
        log.info("Выполняю регистрацию пользователя ");
        authService.completeRegistration(providerUserInfoEncodedJson, request);
        return redirectToLk();
    }

    private ResponseEntity<Void> redirectToLk() {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(307))
                .location(crmProperties.getPlatformUri())
                .build();
    }
}
