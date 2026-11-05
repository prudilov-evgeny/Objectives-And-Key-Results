package ru.cohenrol.objectivesandkeyresults.domain.service;

import org.junit.jupiter.api.Test;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.IssueEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.RunEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.UnitEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.GoalRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.IssueRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.RunRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.UnitRepository;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;
import ru.cohenrol.objectivesandkeyresults.domain.model.response.GetSummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SummaryServiceTest {

    private final UnitRepository unitRepo = mock(UnitRepository.class);
    private final GoalRepository goalRepo = mock(GoalRepository.class);
    private final IssueRepository issueRepo = mock(IssueRepository.class);
    private final RunRepository runRepo = mock(RunRepository.class);
    private final DeltaService deltaService = mock(DeltaService.class);

    private final SummaryService service = new SummaryService(
        unitRepo, goalRepo, issueRepo, runRepo, deltaService);

    @Test
    void criticalIssuesCount_twoProblems() {
        stubRunAndUnit(null);

        IssueEntity c1 = issue(Severity.CRITICAL);
        IssueEntity c2 = issue(Severity.CRITICAL);
        IssueEntity m1 = issue(Severity.MAJOR);
        when(issueRepo.findIssuesByRunAndUnitIds(eq("RUN-002"), anyList()))
            .thenReturn(List.of(c1, c2, m1));

        GetSummaryResponse resp = service.getSummary("RUN-002", "DEP-1");
        GetSummaryResponse.Metric metric = metric(resp, "critical_issues_count");

        assertEquals(0, metric.value().compareTo(BigDecimal.valueOf(2)));
        assertNull(metric.previous());
        assertNull(metric.delta());
        assertNull(metric.trend());
    }

    @Test
    void issuesTotal_countsAll() {
        stubRunAndUnit(null);

        when(issueRepo.findIssuesByRunAndUnitIds(eq("RUN-002"), anyList()))
            .thenReturn(List.of(
                issue(Severity.CRITICAL),
                issue(Severity.MAJOR),
                issue(Severity.MINOR)
            ));

        GetSummaryResponse resp = service.getSummary("RUN-002", "DEP-1");
        GetSummaryResponse.Metric metric = metric(resp, "issues_total");

        assertEquals(0, metric.value().compareTo(BigDecimal.valueOf(3)));
    }

    @Test
    void previousRun_deltaAndTrend() {
        RunEntity prev = new RunEntity();
        prev.setId("RUN-001");
        prev.setSnapshotDate(LocalDate.of(2026, 10, 19));

        RunEntity run = new RunEntity();
        run.setId("RUN-002");
        run.setSnapshotDate(LocalDate.of(2026, 10, 26));
        run.setComparedTo(prev);

        when(runRepo.findRunById("RUN-002")).thenReturn(Optional.of(run));

        UnitEntity unit = new UnitEntity();
        unit.setId("DEP-1");
        unit.setName("Dep 1 name");
        when(unitRepo.findUnitById("DEP-1")).thenReturn(Optional.of(unit));
        when(unitRepo.findSubtreeOfUnitIds("DEP-1")).thenReturn(List.of("DEP-1"));
        when(goalRepo.findGoalsByUnitIds(anyList())).thenReturn(List.of());

        when(issueRepo.findIssuesByRunAndUnitIds(eq("RUN-002"), anyList()))
            .thenReturn(List.of(issue(Severity.CRITICAL), issue(Severity.CRITICAL)));
        when(issueRepo.findIssuesByRunAndUnitIds(eq("RUN-001"), anyList()))
            .thenReturn(List.of(issue(Severity.CRITICAL)));

        GetSummaryResponse resp = service.getSummary("RUN-002", "DEP-1");
        GetSummaryResponse.Metric metric = metric(resp, "critical_issues_count");

        assertEquals(0, metric.value().compareTo(BigDecimal.valueOf(2)));
        assertEquals(0, metric.previous().compareTo(BigDecimal.ONE));
        assertEquals(0, metric.delta().compareTo(BigDecimal.ONE));
        assertEquals("worse", metric.trend());
    }

    private void stubRunAndUnit(RunEntity previous) {
        RunEntity run = new RunEntity();
        run.setId("RUN-002");
        run.setSnapshotDate(LocalDate.of(2026, 10, 26));
        run.setComparedTo(previous);
        when(runRepo.findRunById("RUN-002")).thenReturn(Optional.of(run));

        UnitEntity unit = new UnitEntity();
        unit.setId("DEP-1");
        unit.setName("Dep 1 name");
        when(unitRepo.findUnitById("DEP-1")).thenReturn(Optional.of(unit));
        when(unitRepo.findSubtreeOfUnitIds("DEP-1")).thenReturn(List.of("DEP-1"));
        when(goalRepo.findGoalsByUnitIds(anyList())).thenReturn(List.of());
    }

    private IssueEntity issue(Severity sev) {
        IssueEntity i = new IssueEntity();
        i.setSeverity(sev);
        return i;
    }

    private GetSummaryResponse.Metric metric(GetSummaryResponse resp, String key) {
        return resp.metrics().stream()
            .filter(m -> key.equals(m.key()))
            .findFirst()
            .orElseThrow();
    }
}