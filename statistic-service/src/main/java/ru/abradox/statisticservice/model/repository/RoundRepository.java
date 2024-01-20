package ru.abradox.statisticservice.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.abradox.client.statistic.StatusRound;
import ru.abradox.statisticservice.model.entity.RoundEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoundRepository extends JpaRepository<RoundEntity, UUID> {

    @Query("SELECT r FROM RoundEntity r " +
            "JOIN FETCH r.topBot " +
            "JOIN FETCH r.downBot " +
            "WHERE r.status = :status AND r.begin < :beforeTime")
    List<RoundEntity> findRoundsByStatusBeforeGivenTime(StatusRound status, LocalDateTime beforeTime);

    @Query("SELECT r FROM RoundEntity r " +
            "JOIN FETCH r.topBot " +
            "JOIN FETCH r.downBot " +
            "WHERE r.id = :id")
    Optional<RoundEntity> findByIdWithBots(UUID id);

    @Query("SELECT r FROM RoundEntity r " +
            "JOIN FETCH r.topBot tb " +
            "JOIN FETCH r.downBot db " +
            "WHERE r.status = 'WAIT' AND r.type = 'PROD' AND tb.isPlay = false")
    List<RoundEntity> findWaitProdRoundsWithNotPlayBots();
}
