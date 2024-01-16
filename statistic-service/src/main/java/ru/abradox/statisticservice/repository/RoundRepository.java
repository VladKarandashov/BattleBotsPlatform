package ru.abradox.statisticservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abradox.statisticservice.entity.RoundEntity;

import java.util.UUID;

public interface RoundRepository extends JpaRepository<RoundEntity, UUID> {
}
