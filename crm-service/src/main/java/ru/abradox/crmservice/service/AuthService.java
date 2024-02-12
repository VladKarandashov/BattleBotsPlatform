package ru.abradox.crmservice.service;

import ru.abradox.client.crm.request.CompleteRegistrationRequest;
import ru.abradox.platformapi.crm.UserInfo;

public interface AuthService {

    UserInfo getUserInfo(String providerUserId);

    void updateUserInfo(String providerUserInfoEncodedJson);

    void completeRegistration(String providerUserInfoEncodedJson, CompleteRegistrationRequest request);

}
