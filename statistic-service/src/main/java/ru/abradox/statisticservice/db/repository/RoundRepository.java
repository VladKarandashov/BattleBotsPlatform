package ru.abradox.statisticservice.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.abradox.client.statistic.StatusRound;
import ru.abradox.statisticservice.db.entity.RoundEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RoundRepository extends JpaRepository<RoundEntity, UUID> {

    @Query("SELECT r FROM RoundEntity r WHERE r.status = :status AND r.begin < :beforeTime")
    List<RoundEntity> findRoundsByStatusBeforeGivenTime(StatusRound status, LocalDateTime beforeTime);
}
