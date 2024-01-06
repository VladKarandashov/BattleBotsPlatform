package ru.abradox.crmservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abradox.crmservice.dto.request.CompleteRegistrationRequest;
import ru.abradox.crmservice.service.AuthService;
import ru.abradox.dto.UserInfo;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public UserInfo getUserInfo(String providerUserId) {
        return null;
    }

    @Override
    public void updateUserInfo(String providerUserInfoEncodedJson) {

    }

    @Override
    public void completeRegistration(String providerUserInfoEncodedJson, CompleteRegistrationRequest request) {

    }
}
