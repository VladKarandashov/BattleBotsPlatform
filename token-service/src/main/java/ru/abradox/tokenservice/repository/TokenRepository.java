package ru.abradox.tokenservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abradox.tokenservice.entity.TokenEntity;

import java.util.UUID;

public interface TokenRepository extends JpaRepository<TokenEntity, UUID>  {
}
