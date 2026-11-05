package ru.cohenrol.objectivesandkeyresults.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.cohenrol.objectivesandkeyresults.domain.enums.GoalStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
public class GoalEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitEntity unitEntity;

    @Column(name = "owner")
    private String owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id")
    private GoalEntity parentGoalEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GoalStatus status;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @OneToMany(mappedBy = "goalEntity", fetch = FetchType.LAZY)
    private List<KeyResultEntity> keyResultEntities = new ArrayList<>();
}