package ru.abradox.crmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abradox.crmservice.dto.request.CompleteRegistrationRequest;
import ru.abradox.crmservice.repository.UserRepository;
import ru.abradox.crmservice.service.AuthService;
import ru.abradox.dto.UserInfo;
import ru.abradox.exception.BusinessRedirectException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public UserInfo getUserInfo(String providerUserId) {
        var user = userRepository.findByProviderId(providerUserId)
                .orElseThrow(() -> {
                    log.info("Не найден user с providerId={} -> отправляю на регистрацию", providerUserId);
                    return new BusinessRedirectException("/registration", 307);
                });
        if (user.isBlocked()) {
            log.info("User с providerId={} ЗАБЛОКИРОВАН -> отправляю на страницу блока", providerUserId);
            throw new BusinessRedirectException("/blocked", 301);
        }
        return UserInfo.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public void updateUserInfo(String providerUserInfoEncodedJson) {

    }

    @Override
    public void completeRegistration(String providerUserInfoEncodedJson, CompleteRegistrationRequest request) {

    }
}
