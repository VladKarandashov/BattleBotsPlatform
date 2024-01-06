package ru.abradox.crmservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;
import ru.abradox.crmservice.dto.request.CompleteRegistrationRequest;
import ru.abradox.crmservice.entity.UserEntity;
import ru.abradox.crmservice.repository.UserRepository;
import ru.abradox.crmservice.service.AuthService;
import ru.abradox.dto.ProviderUserInfo;
import ru.abradox.dto.UserInfo;
import ru.abradox.exception.BusinessRedirectException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    // TODO за кэшировать результат, кроме ("/registration", 307)
    public UserInfo getUserInfo(String providerUserId) {
        var user = findUserByProviderId(providerUserId);
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
        var providerUserInfo = processProviderUserInfo(providerUserInfoEncodedJson);
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
        var providerUserInfo = processProviderUserInfo(providerUserInfoEncodedJson);
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
                    return new BusinessRedirectException("/registration", 307);
                });
    }

    @SneakyThrows
    private ProviderUserInfo processProviderUserInfo(String providerUserInfoEncodedJson) {
        var providerUserInfoJson = UriEncoder.decode(providerUserInfoEncodedJson);
        var providerUserInfo = new ObjectMapper().readValue(providerUserInfoJson, ProviderUserInfo.class);
        log.info("Получил providerUserInfo={}", providerUserInfo);
        return providerUserInfo;
    }
}
