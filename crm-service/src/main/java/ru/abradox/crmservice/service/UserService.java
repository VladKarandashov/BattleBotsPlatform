package ru.abradox.crmservice.service;

import org.springframework.http.ResponseEntity;

public interface UserService {

    /**
     * Проверяет возможность входа пользователя на платформу.
     *
     * @param userId id пользователя
     */
    ResponseEntity<Void> checkUserAuth(String userId);
}
