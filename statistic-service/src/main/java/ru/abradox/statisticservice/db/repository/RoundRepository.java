package ru.abradox.statisticservice.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abradox.statisticservice.db.entity.RoundEntity;

import java.util.UUID;

public interface RoundRepository extends JpaRepository<RoundEntity, UUID> {
}
