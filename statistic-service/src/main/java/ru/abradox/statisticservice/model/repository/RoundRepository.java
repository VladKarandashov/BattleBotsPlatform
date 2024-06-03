package ru.abradox.statisticservice.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.abradox.platformapi.battle.StatusRound;
import ru.abradox.platformapi.battle.TypeRound;
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
            "WHERE r.status = 'WAIT' AND r.type = 'PROD' AND tb.isPlay = false AND db.isPlay = false")
    List<RoundEntity> findWaitProdRoundsWithNotPlayBots();

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN false ELSE true END " +
            "FROM RoundEntity r " +
            "WHERE r.type = :type " +
            "AND r.status <> :status")
    Boolean areAllRoundsWithGivenTypeHaveGivenStatus(TypeRound type, StatusRound status);

    @Query("SELECT r FROM RoundEntity r " +
            "JOIN FETCH r.topBot " +
            "JOIN FETCH r.downBot " +
            "WHERE r.type = :type")
    List<RoundEntity> findRoundsByStatus(TypeRound type);

    @Modifying
    void deleteAllByType(TypeRound type);
}
