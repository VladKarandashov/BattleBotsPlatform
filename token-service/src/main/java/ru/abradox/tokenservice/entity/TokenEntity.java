package ru.abradox.tokenservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.abradox.platformapi.token.TypeToken;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Token")
public class TokenEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * Название бота
     */
    @Column(name = "title", length = 16, nullable = false)
    private String title;

    /**
     * Тип токена (тестовый или реальный)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    private TypeToken type;

    @Column(name = "blocked")
    private boolean blocked;

    public TokenEntity(Integer userId, String title, TypeToken type) {
        this.userId = userId;
        this.title = title;
        this.type = type;
    }
}