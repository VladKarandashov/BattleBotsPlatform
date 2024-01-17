package ru.abradox.battleservice.model;

import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.UUID;

public interface RoundRepository extends KeyValueRepository<RoundState, UUID> {
}