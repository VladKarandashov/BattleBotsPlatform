package ru.abradox.crmservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.abradox.client.crm.request.CompleteRegistrationRequest;
import ru.abradox.common.response.GenericResponse;
import ru.abradox.crmservice.service.AuthService;
import ru.abradox.exception.BusinessException;
import ru.abradox.exception.ExceptionStatus;
import ru.abradox.platformapi.crm.UserInfo;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

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
    public GenericResponse<UserInfo> getUserInfo(@PathVariable("id") String providerUserId) {
        log.info("Пришёл запрос на получение информации пользователя {}", providerUserId);
        return new GenericResponse<>(authService.getUserInfo(providerUserId));
    }

    /**
     * Завершает процедуру входа на платформу и перенаправляет в личный кабинет
     *
     * @param providerUserInfoEncodedJson информация от id-провайдера
     */
    @PostMapping("/api/v1/auth")
    public void authorize(@RequestHeader("user") String providerUserInfoEncodedJson) {
        log.info("Выполняю вход пользователя {}", providerUserInfoEncodedJson);
        authService.updateUserInfo(providerUserInfoEncodedJson);
        throw new BusinessException(ExceptionStatus.PLATFORM_REDIRECT);
    }

    /**
     * Окончание регистрации. Сохранение данных пользователя
     *
     * @param providerUserInfoEncodedJson информация от id-провайдера
     * @param request                     данные формы
     */
    @PostMapping("/api/v1/auth/completeRegistration")
    public void completeRegistration(
            @RequestHeader("user") String providerUserInfoEncodedJson,
            @Valid @RequestBody CompleteRegistrationRequest request) {
        log.info("Выполняю регистрацию пользователя ");
        authService.completeRegistration(providerUserInfoEncodedJson, request);
        throw new BusinessException(ExceptionStatus.PLATFORM_REDIRECT);
    }
}
