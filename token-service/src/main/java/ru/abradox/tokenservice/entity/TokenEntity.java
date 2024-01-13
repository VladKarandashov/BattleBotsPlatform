package ru.abradox.tokenservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "token")
public class TokenEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "title", length = 16, nullable = false)
    private String title;

    @Column(name = "type", length = 16, nullable = false)
    private String type;

    @Column(name = "shoulder", length = 16, nullable = false)
    private String shoulder;

    @Column(name = "blocked")
    private boolean blocked;

}