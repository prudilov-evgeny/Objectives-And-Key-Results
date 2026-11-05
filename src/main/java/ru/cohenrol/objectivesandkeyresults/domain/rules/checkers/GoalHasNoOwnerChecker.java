package ru.cohenrol.objectivesandkeyresults.domain.rules.checkers;

import org.springframework.stereotype.Component;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;
import ru.cohenrol.objectivesandkeyresults.domain.enums.ObjectType;
import ru.cohenrol.objectivesandkeyresults.domain.enums.RuleCode;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;
import ru.cohenrol.objectivesandkeyresults.domain.rules.RuleChecker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class GoalHasNoOwnerChecker implements RuleChecker {

    @Override
    public List<FoundIssue> check(
        List<GoalEntity> goalEntities,
        List<KeyResultEntity> keyResultEntities,
        LocalDate snapshotDate
    ) {
        List<FoundIssue> issues = new ArrayList<>();
        for (GoalEntity goalEntity : goalEntities) {
            if (goalEntity.getOwner() == null || goalEntity.getOwner().isBlank()) {
                issues.add(new FoundIssue(
                    RuleCode.GOAL_NO_OWNER,
                    ObjectType.GOAL,
                    goalEntity.getId(),
                    Severity.CRITICAL
                ));
            }
        }
        return issues;
    }
}
