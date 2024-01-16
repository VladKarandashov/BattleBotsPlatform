package ru.abradox.statisticservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.abradox.statisticservice.entity.BotEntity;

import java.util.Set;
import java.util.UUID;

public interface BotRepository extends JpaRepository<BotEntity, Integer> {

    @Modifying
    @Query("UPDATE BotEntity b SET b.isActive = CASE WHEN b.token IN :activeTokens THEN true ELSE false END")
    void setActiveStatusesByActiveTokens(Set<UUID> activeTokens);

    @Query("SELECT b.token FROM BotEntity b")
    Set<UUID> findAllTokens();
}
