package ru.abradox.statisticservice.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.abradox.client.statistic.ResultRound;
import ru.abradox.client.statistic.StatusRound;
import ru.abradox.client.statistic.TypeRound;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "round")
public class RoundEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "top_bot_id", referencedColumnName = "id")
    private BotEntity topBot;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "down_bot_id", referencedColumnName = "id")
    private BotEntity downBot;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeRound type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusRound status;

    @Column(name = "begin_time")
    private LocalDateTime begin;

    @Column(name = "end_time")
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private ResultRound result;
}
