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
public class KeyResultsStaleProgressChecker implements RuleChecker {
    private static final int STALE_DAYS = 45;

    @Override
    public List<FoundIssue> check(
        List<GoalEntity> goalEntities,
        List<KeyResultEntity> keyResultEntities,
        LocalDate snapshotDate
    ) {
        LocalDate threshold = snapshotDate.minusDays(STALE_DAYS);

        List<FoundIssue> issues = new ArrayList<>();
        for (KeyResultEntity kr : keyResultEntities) {
            LocalDate updated = kr.getFactUpdatedAt();
            if (updated == null || updated.isBefore(threshold)) {
                issues.add(new FoundIssue(
                    RuleCode.KR_STALE_PROGRESS,
                    ObjectType.KR,
                    kr.getId(),
                    Severity.MINOR
                ));
            }
        }
        return issues;
    }
}