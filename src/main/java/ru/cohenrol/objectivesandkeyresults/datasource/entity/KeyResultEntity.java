package ru.cohenrol.objectivesandkeyresults.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "key_results")
@Data
@NoArgsConstructor
public class KeyResultEntity {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goal_id", nullable = false)
    private GoalEntity goalEntity;

    @Column(nullable = false)
    private String title;

    @Column(name = "target_value")
    private BigDecimal targetValue;

    @Column(name = "fact_value")
    private BigDecimal factValue;

    @Column(nullable = false)
    private BigDecimal weight;

    @Column(name = "fact_updated_at")
    private LocalDate factUpdatedAt;
}