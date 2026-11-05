package ru.cohenrol.objectivesandkeyresults.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.cohenrol.objectivesandkeyresults.domain.enums.RunStatus;

import java.time.LocalDate;

@Entity
@Table(name = "runs")
@Data
@NoArgsConstructor
public class RunEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private RunStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compared_to_run_id")
    private RunEntity comparedTo;

    @Column(name = "issues_count", nullable = false)
    private int issuesCount;
}