package ru.abradox.battleservice.model;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.abradox.platformapi.battle.StatusRound;

import java.util.List;
import java.util.UUID;

public interface RoundRepository extends KeyValueRepository<RoundState, UUID> {
    List<RoundState> findAllByStatus(StatusRound status);
}