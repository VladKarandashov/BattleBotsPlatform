package ru.abradox.tokenservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abradox.client.token.TypeToken;
import ru.abradox.tokenservice.entity.TokenEntity;

import java.util.List;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {

    List<TokenEntity> findAllByBlockedIsFalse();

    TokenEntity findByIdAndBlockedIsFalse(UUID id);

    List<TokenEntity> findAllByUserIdAndBlockedIsFalse(Integer userId);

    List<TokenEntity> findAllByTitleAndBlockedIsFalse(String title);

    Integer countAllByUserIdAndType(Integer userId, TypeToken typeToken);

    Boolean existsByTitle(String title);

}
