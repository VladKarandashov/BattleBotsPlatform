package ru.abradox.crmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abradox.crmservice.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByProviderId(String providerId);

    boolean existsByProviderId(String providerId);

    boolean existsByNickName(String nickName);
}
