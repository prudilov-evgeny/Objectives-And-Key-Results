package ru.cohenrol.objectivesandkeyresults.domain.rules.checkers;

import org.junit.jupiter.api.Test;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyResultsWeightsSumCheckerTestEntity {

    private final KeyResultsWeightsSumChecker checker = new KeyResultsWeightsSumChecker();
    private final LocalDate snapshot = LocalDate.of(2026, 10, 26);

    private KeyResultEntity kr(GoalEntity goalEntity, String weight) {
        KeyResultEntity kr = new KeyResultEntity();
        kr.setGoalEntity(goalEntity);
        kr.setWeight(new BigDecimal(weight));
        return kr;
    }

    @Test
    void sumExactly100_noProblem() {
        GoalEntity g = new GoalEntity();
        g.setId("G1");
        List<KeyResultEntity> krs = List.of(kr(g, "60"), kr(g, "40"));

        assertTrue(checker.check(List.of(g), krs, snapshot).isEmpty());
    }

    @Test
    void sum99_99_problem() {
        GoalEntity g = new GoalEntity();
        g.setId("G1");
        List<KeyResultEntity> krs = List.of(kr(g, "60"), kr(g, "39.99"));

        List<FoundIssue> issues = checker.check(List.of(g), krs, snapshot);
        assertEquals(1, issues.size());
        assertEquals("G1", issues.get(0).objectId());
    }

    @Test
    void sum100_01_problem() {
        GoalEntity g = new GoalEntity();
        g.setId("G1");
        List<KeyResultEntity> krs = List.of(kr(g, "60"), kr(g, "40.01"));

        List<FoundIssue> issues = checker.check(List.of(g), krs, snapshot);
        assertEquals(1, issues.size());
    }
}