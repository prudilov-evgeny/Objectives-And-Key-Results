package ru.cohenrol.objectivesandkeyresults.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;

@Entity
@Table(name = "rules")
@Data
@NoArgsConstructor
public class RuleEntity {

    @Id
    @Column(length = 64)
    private String code;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Severity severity;
}