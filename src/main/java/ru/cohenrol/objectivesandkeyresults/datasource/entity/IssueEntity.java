package ru.cohenrol.objectivesandkeyresults.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.cohenrol.objectivesandkeyresults.domain.enums.ObjectType;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;

@Entity
@Table(name = "issues")
@Data
@NoArgsConstructor
public class IssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false)
    private RunEntity runEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_code", nullable = false)
    private RuleEntity rule;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false, length = 8)
    private ObjectType objectType;

    @Column(name = "object_id", nullable = false, length = 64)
    private String objectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Severity severity;
}