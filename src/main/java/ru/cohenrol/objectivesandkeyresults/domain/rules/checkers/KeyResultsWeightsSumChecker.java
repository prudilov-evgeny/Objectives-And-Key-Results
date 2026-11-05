package ru.cohenrol.objectivesandkeyresults.domain.rules.checkers;

import org.springframework.stereotype.Component;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;
import ru.cohenrol.objectivesandkeyresults.domain.enums.ObjectType;
import ru.cohenrol.objectivesandkeyresults.domain.enums.RuleCode;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;
import ru.cohenrol.objectivesandkeyresults.domain.rules.RuleChecker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class KeyResultsWeightsSumChecker implements RuleChecker {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TOLERANCE = new BigDecimal("0.01");

    @Override
    public List<FoundIssue> check(
        List<GoalEntity> goalEntities,
        List<KeyResultEntity> keyResultEntities,
        LocalDate snapshotDate
    ) {
        Map<String, List<KeyResultEntity>> keyResultsByGoal = keyResultEntities.stream()
            .collect(Collectors.groupingBy(kr -> kr.getGoalEntity().getId()));

        List<FoundIssue> issues = new ArrayList<>();
        for (GoalEntity goalEntity : goalEntities) {
            List<KeyResultEntity> groupedKeyResultEntities = keyResultsByGoal.get(goalEntity.getId());

            if (groupedKeyResultEntities == null || groupedKeyResultEntities.isEmpty()) {
                continue;
            }

            BigDecimal sum = groupedKeyResultEntities.stream()
                .map(KeyResultEntity::getWeight)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (sum.subtract(HUNDRED).abs().compareTo(TOLERANCE) >= 0) {
                issues.add(new FoundIssue(
                    RuleCode.KR_WEIGHTS_SUM,
                    ObjectType.GOAL,
                    goalEntity.getId(),
                    Severity.MAJOR
                ));
            }
        }
        return issues;
    }
}