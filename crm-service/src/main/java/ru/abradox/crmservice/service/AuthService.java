package ru.abradox.crmservice.service;

import ru.abradox.crmservice.dto.request.CompleteRegistrationRequest;
import ru.abradox.dto.UserInfo;

public interface AuthService {

    UserInfo getUserInfo(String providerUserId);

    void updateUserInfo(String providerUserInfoEncodedJson);

    void completeRegistration(String providerUserInfoEncodedJson, CompleteRegistrationRequest request);

}
