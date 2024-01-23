package ru.abradox.statisticservice.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abradox.statisticservice.model.entity.HistoryEntity;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer>  {
}
