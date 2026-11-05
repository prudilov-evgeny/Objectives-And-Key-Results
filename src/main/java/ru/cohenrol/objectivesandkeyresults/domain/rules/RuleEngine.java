package ru.cohenrol.objectivesandkeyresults.domain.rules;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RuleEngine {

    private final List<RuleChecker> checkers;

    public List<FoundIssue> run(
        List<GoalEntity> goalEntities,
        List<KeyResultEntity> keyResultEntities,
        LocalDate snapshotDate
    ) {
        List<FoundIssue> all = new ArrayList<>();
        for (RuleChecker checker : checkers) {
            all.addAll(checker.check(goalEntities, keyResultEntities, snapshotDate));
        }
        return all;
    }
}
