package ru.abradox.statisticservice.model.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import ru.abradox.platformapi.statistic.RoundResult;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "history")
public class HistoryEntity {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "history_seq_id", sequenceName = "history_seq_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq_id")
    private Integer id;

    @Type(JsonType.class)
    private List<Integer> orderedBots;

    @Type(JsonType.class)
    private List<RoundResult> roundResults;

    public HistoryEntity(List<Integer> orderedBots, List<RoundResult> roundResults) {
        this.orderedBots = orderedBots;
        this.roundResults = roundResults;
    }
}
