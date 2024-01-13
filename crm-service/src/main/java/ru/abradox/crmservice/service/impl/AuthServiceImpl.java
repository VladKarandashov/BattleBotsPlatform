package ru.abradox.crmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abradox.crmservice.config.CrmProperties;
import ru.abradox.crmservice.dto.request.CompleteRegistrationRequest;
import ru.abradox.crmservice.entity.UserEntity;
import ru.abradox.crmservice.repository.UserRepository;
import ru.abradox.crmservice.service.AuthService;
import ru.abradox.dto.ProviderUserInfo;
import ru.abradox.dto.UserInfo;
import ru.abradox.exception.BusinessException;
import ru.abradox.exception.BusinessRedirectException;

@Slf4j
@Service
@RequiredArgsConstructor
// TODO внедрить mapstruct
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CrmProperties crmProperties;

    @Override
    public UserInfo getUserInfo(String providerUserId) {
        var user = findUserByProviderId(providerUserId);
        if (user.isBlocked()) {
            log.info("User с providerId={} ЗАБЛОКИРОВАН -> отправляю на страницу блока", providerUserId);
            throw new BusinessRedirectException("/crm/view/blocked", 301);
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
        var providerUserInfo = ProviderUserInfo.parseProviderUserInfo(providerUserInfoEncodedJson);
        log.info("Получил providerUserInfo={}", providerUserInfo);
        var user = findUserByProviderId(providerUserInfo);
        user.setLogin(user.getLogin());
        user.setEmail(user.getEmail());
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setFullName(user.getFullName());
        userRepository.save(user);
    }

    @Override
    public void completeRegistration(String providerUserInfoEncodedJson, CompleteRegistrationRequest request) {
        var providerUserInfo = ProviderUserInfo.parseProviderUserInfo(providerUserInfoEncodedJson);
        log.info("Получил providerUserInfo={}", providerUserInfo);
        if (userRepository.existsByProviderId(providerUserInfo.getProviderId())) {
            throw new BusinessRedirectException(crmProperties.getPlatformUri(), 307);
        }
        if (userRepository.existsByNickName(request.getNickName())) {
            throw new BusinessException(1408, "Такой nickname уже существует");
        }
        // TODO валидация телеграмма
        var user = UserEntity.builder()
                .providerId(providerUserInfo.getProviderId())
                .login(providerUserInfo.getLogin())
                .email(providerUserInfo.getEmail())
                .nickName(request.getNickName())
                .firstName(providerUserInfo.getFirstName())
                .lastName(providerUserInfo.getLastName())
                .fullName(providerUserInfo.getFullName())
                .blocked(false)
                .build();
        userRepository.save(user);
    }

    private UserEntity findUserByProviderId(ProviderUserInfo providerUserInfo) {
        return findUserByProviderId(providerUserInfo.getProviderId());
    }

    private UserEntity findUserByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId)
                .orElseThrow(() -> {
                    log.info("Не найден user с providerId={} -> отправляю на регистрацию", providerId);
                    return new BusinessRedirectException("/crm/view/registration", 307);
                });
    }
}
