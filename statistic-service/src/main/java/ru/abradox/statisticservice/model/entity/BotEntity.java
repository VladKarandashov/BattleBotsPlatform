package ru.abradox.statisticservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.abradox.platformapi.token.TypeToken;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bot")
public class BotEntity {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "bot_seq_id", sequenceName = "bot_seq_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bot_seq_id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String title;

    @Column(name = "token")
    private UUID token;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeToken type;

    @Column(name = "position")
    private Integer position;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_play")
    private Boolean isPlay;
}
