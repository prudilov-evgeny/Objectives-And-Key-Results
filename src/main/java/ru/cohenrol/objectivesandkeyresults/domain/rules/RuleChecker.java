package ru.cohenrol.objectivesandkeyresults.domain.rules;

import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;

import java.time.LocalDate;
import java.util.List;

public interface RuleChecker {
    List<FoundIssue> check(
        List<GoalEntity> goalEntities,
        List<KeyResultEntity> keyResultEntities,
        LocalDate snapshotDate
    );
}