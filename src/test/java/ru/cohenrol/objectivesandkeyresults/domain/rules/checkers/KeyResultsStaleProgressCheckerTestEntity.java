package ru.cohenrol.objectivesandkeyresults.domain.rules.checkers;

import org.junit.jupiter.api.Test;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyResultsStaleProgressCheckerTestEntity {

    private final KeyResultsStaleProgressChecker checker = new KeyResultsStaleProgressChecker();
    private final LocalDate snapshot = LocalDate.of(2026, 10, 26);

    private KeyResultEntity kr(String id, LocalDate updated) {
        KeyResultEntity kr = new KeyResultEntity();
        kr.setId(id);
        kr.setFactUpdatedAt(updated);
        return kr;
    }

    @Test
    void exactly45Days_notStale() {
        KeyResultEntity kr = kr("KR1", snapshot.minusDays(45));

        assertTrue(checker.check(List.of(), List.of(kr), snapshot).isEmpty());
    }

    @Test
    void _46Days_stale() {
        KeyResultEntity kr = kr("KR2", snapshot.minusDays(46));

        List<FoundIssue> issues = checker.check(List.of(), List.of(kr), snapshot);
        assertEquals(1, issues.size());
        assertEquals("KR2", issues.get(0).objectId());
    }

    @Test
    void nullUpdatedAt_stale() {
        KeyResultEntity kr = kr("KR3", null);

        List<FoundIssue> issues = checker.check(List.of(), List.of(kr), snapshot);
        assertEquals(1, issues.size());
    }
}