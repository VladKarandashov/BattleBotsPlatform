package ru.abradox.statisticservice.model.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.abradox.statisticservice.model.entity.HistoryEntity;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer>  {

    Optional<HistoryEntity> findTopByOrderByIdDesc();

    @Query(value = "SELECT e FROM HistoryEntity e ORDER BY e.id DESC")
    List<HistoryEntity> findLast10ByOrderByIdDesc(Pageable pageable);
}
