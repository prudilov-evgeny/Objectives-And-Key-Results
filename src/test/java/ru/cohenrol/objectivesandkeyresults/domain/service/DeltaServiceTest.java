package ru.cohenrol.objectivesandkeyresults.domain.service;

import org.junit.jupiter.api.Test;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.IssueRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.view.IssueKeyView;
import ru.cohenrol.objectivesandkeyresults.domain.model.Delta;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeltaServiceTest {

    private final IssueRepository repo = mock(IssueRepository.class);
    private final DeltaService service = new DeltaService(repo);

    private IssueKeyView key(String rule, String object) {
        IssueKeyView v = mock(IssueKeyView.class);
        when(v.getRuleCode()).thenReturn(rule);
        when(v.getObjectId()).thenReturn(object);
        return v;
    }

    @Test
    void newPersistingResolved() {
        IssueKeyView a = key("GOAL_NO_OWNER", "G1");
        IssueKeyView b = key("GOAL_NO_KR", "G2");
        IssueKeyView c = key("KR_WEIGHTS_SUM", "G3");

        IssueKeyView d = key("GOAL_NO_OWNER", "G1");
        IssueKeyView e = key("GOAL_NO_KR", "G2");
        IssueKeyView f = key("KR_STALE_PROGRESS", "KR4");

        when(repo.findIssueKeysByRunId("RUN-001")).thenReturn(List.of(a, b, c));
        when(repo.findIssueKeysByRunId("RUN-002")).thenReturn(List.of(d, e, f));

        Delta delta = service.calculate("RUN-002", "RUN-001");

        assertEquals(1, delta.newCount());
        assertEquals(2, delta.persisting());
        assertEquals(1, delta.resolved());
    }

    @Test
    void firstRun_allNull() {
        Delta delta = service.calculate("RUN-001", null);

        assertNull(delta.newCount());
        assertNull(delta.persisting());
        assertNull(delta.resolved());
    }

    @Test
    void identicalSets_allPersisting() {
        IssueKeyView a = key("GOAL_NO_OWNER", "G1");
        IssueKeyView b = key("GOAL_NO_KR", "G2");
        List<IssueKeyView> keys = List.of(a, b);

        when(repo.findIssueKeysByRunId("RUN-001")).thenReturn(keys);
        when(repo.findIssueKeysByRunId("RUN-002")).thenReturn(keys);

        Delta delta = service.calculate("RUN-002", "RUN-001");

        assertEquals(0, delta.newCount());
        assertEquals(2, delta.persisting());
        assertEquals(0, delta.resolved());
    }

    @Test
    void currentEmpty_allResolved() {
        IssueKeyView a = key("GOAL_NO_OWNER", "G1");
        IssueKeyView b = key("GOAL_NO_KR", "G2");

        when(repo.findIssueKeysByRunId("RUN-001")).thenReturn(List.of(a, b));
        when(repo.findIssueKeysByRunId("RUN-002")).thenReturn(List.of());

        Delta delta = service.calculate("RUN-002", "RUN-001");

        assertEquals(0, delta.newCount());
        assertEquals(0, delta.persisting());
        assertEquals(2, delta.resolved());
    }

    @Test
    void previousEmpty_allNew() {
        IssueKeyView a = key("GOAL_NO_OWNER", "G1");
        IssueKeyView b = key("GOAL_NO_KR", "G2");

        when(repo.findIssueKeysByRunId("RUN-001")).thenReturn(List.of());
        when(repo.findIssueKeysByRunId("RUN-002")).thenReturn(List.of(a, b));

        Delta delta = service.calculate("RUN-002", "RUN-001");

        assertEquals(2, delta.newCount());
        assertEquals(0, delta.persisting());
        assertEquals(0, delta.resolved());
    }

    @Test
    void bothEmpty_allZero() {
        when(repo.findIssueKeysByRunId("RUN-001")).thenReturn(List.of());
        when(repo.findIssueKeysByRunId("RUN-002")).thenReturn(List.of());

        Delta delta = service.calculate("RUN-002", "RUN-001");

        assertEquals(0, delta.newCount());
        assertEquals(0, delta.persisting());
        assertEquals(0, delta.resolved());
    }
}