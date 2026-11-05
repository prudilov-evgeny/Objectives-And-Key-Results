package ru.cohenrol.objectivesandkeyresults.domain.rules.checkers;

import org.junit.jupiter.api.Test;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoalEntityHasNoKeyResultsCheckerTestEntity {

    private final GoalHasNoKeyResultsChecker checker = new GoalHasNoKeyResultsChecker();
    private final LocalDate snapshot = LocalDate.of(2026, 10, 26);

    @Test
    void goalWithoutKr_problem() {
        GoalEntity g = new GoalEntity();
        g.setId("G1");

        List<FoundIssue> issues = checker.check(List.of(g), List.of(), snapshot);

        assertEquals(1, issues.size());
        assertEquals("G1", issues.get(0).objectId());
    }

    @Test
    void goalWithOneKr_noProblem() {
        GoalEntity g = new GoalEntity();
        g.setId("G1");

        KeyResultEntity kr = new KeyResultEntity();
        kr.setId("KR1");
        kr.setGoalEntity(g);

        List<FoundIssue> issues = checker.check(List.of(g), List.of(kr), snapshot);

        assertTrue(issues.isEmpty());
    }
}