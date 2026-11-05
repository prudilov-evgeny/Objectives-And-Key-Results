package ru.cohenrol.objectivesandkeyresults.domain.rules.checkers;

import org.junit.jupiter.api.Test;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoalEntityHasNoOwnerCheckerTest {

    private final GoalHasNoOwnerChecker checker = new GoalHasNoOwnerChecker();
    private final LocalDate snapshot = LocalDate.of(2026, 10, 26);

    @Test
    void goalWithoutOwner_problem() {
        GoalEntity g = new GoalEntity();
        g.setId("G1");
        g.setOwner(null);

        List<FoundIssue> issues = checker.check(List.of(g), List.of(), snapshot);

        assertEquals(1, issues.size());
        assertEquals("G1", issues.get(0).objectId());
    }

    @Test
    void goalWithOwner_noProblem() {
        GoalEntity g = new GoalEntity();
        g.setId("G1");
        g.setOwner("Иванов");

        List<FoundIssue> issues = checker.check(List.of(g), List.of(), snapshot);

        assertTrue(issues.isEmpty());
    }
}