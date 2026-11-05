package ru.cohenrol.objectivesandkeyresults.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.IssueEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.RunEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.UnitEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.GoalRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.IssueRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.RunRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.UnitRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.view.SeverityCountView;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;
import ru.cohenrol.objectivesandkeyresults.domain.model.Delta;
import ru.cohenrol.objectivesandkeyresults.domain.model.response.GetSummaryResponse;
import ru.cohenrol.objectivesandkeyresults.domain.service.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final UnitRepository unitRepository;
    private final GoalRepository goalRepository;
    private final IssueRepository issueRepository;
    private final RunRepository runRepository;
    private final DeltaService deltaService;

    @Transactional(readOnly = true)
    public GetSummaryResponse getSummary(String runId, String unitId) {
        RunEntity runEntity = runRepository.findRunById(runId)
            .orElseThrow(() -> new NotFoundException("RUN_NOT_FOUND", "run not found: " + runId));
        UnitEntity unitEntity = unitRepository.findUnitById(unitId)
            .orElseThrow(() -> new NotFoundException("UNIT_NOT_FOUND", "unit not found: " + unitId));

        List<String> subtree = unitRepository.findSubtreeOfUnitIds(unitId);
        List<GoalEntity> goalEntities = goalRepository.findGoalsByUnitIds(subtree);
        List<IssueEntity> issueEntities = issueRepository.findIssuesByRunAndUnitIds(runId, subtree);
        RunEntity previous = runEntity.getComparedTo();

        List<IssueEntity> prevIssueEntities = (previous == null) ? List.of() :
            issueRepository.findIssuesByRunAndUnitIds(
                previous.getId(),
                subtree
            );

        List<GetSummaryResponse.Metric> metrics = calculateMetrics(unitEntity, goalEntities, issueEntities, prevIssueEntities, previous);
        List<GetSummaryResponse.SeverityCount> severities = calculateSeverities(runId, previous, subtree);
        Delta delta = (runEntity.getComparedTo() == null) ? Delta.noComparison() :
            deltaService.calculate(
                runEntity.getId(),
                runEntity.getComparedTo().getId()
            );

        return new GetSummaryResponse(
            new GetSummaryResponse.Run(
                runEntity.getId(),
                runEntity.getSnapshotDate()
            ),
            (previous == null) ? null : new GetSummaryResponse.Run(
                previous.getId(),
                previous.getSnapshotDate()
            ),
            new GetSummaryResponse.Unit(
                unitEntity.getId(),
                unitEntity.getName()
            ),
            metrics,
            severities,
            delta
        );
    }

    private List<GetSummaryResponse.Metric> calculateMetrics(
        UnitEntity unitEntity,
        List<GoalEntity> goalEntities,
        List<IssueEntity> issueEntities,
        List<IssueEntity> prevIssueEntities,
        RunEntity previous
    ) {
        boolean hasPrevious = (previous != null);
        List<GetSummaryResponse.Metric> result = new ArrayList<>();
        String unitName = unitEntity.getName();

        long critical = countBySeverity(issueEntities, Severity.CRITICAL);
        long prevCritical = hasPrevious ? countBySeverity(prevIssueEntities, Severity.CRITICAL) : 0;
        result.add(
            countMetric(
                unitName,
                "critical_issues_count", "Critical issues count",
                critical, prevCritical,
                true, hasPrevious
            )
        );

        long major = countBySeverity(issueEntities, Severity.MAJOR);
        long prevMajor = hasPrevious ? countBySeverity(prevIssueEntities, Severity.MAJOR) : 0;
        result.add(
            countMetric(
                unitName,
                "major_issues_count", "Major issues count",
                major, prevMajor,
                true, hasPrevious
            )
        );

        long minor = countBySeverity(issueEntities, Severity.MINOR);
        long prevMinor = hasPrevious ? countBySeverity(prevIssueEntities, Severity.MINOR) : 0;
        result.add(
            countMetric(
                unitName,
                "minor_issues_count", "Minor issues count",
                minor, prevMinor,
                true, hasPrevious
            )
        );

        long total = issueEntities.size();
        long prevTotal = hasPrevious ? prevIssueEntities.size() : 0;
        result.add(
            countMetric(
                unitName,
                "issues_total", "Issues total",
                total, prevTotal,
                true, hasPrevious
            )
        );

        return result;
    }

    private long countBySeverity(List<IssueEntity> issueEntities, Severity severity) {
        return issueEntities.stream().filter(i -> i.getSeverity() == severity).count();
    }

    private GetSummaryResponse.Metric countMetric(
        String unitName,
        String key, String title,
        long current, long previous,
        boolean lowerIsBetter, boolean hasPrevious
    ) {
        BigDecimal v = BigDecimal.valueOf(current);

        if (!hasPrevious) {
            return new GetSummaryResponse.Metric(key, title, unitName, v, null, null, null);
        }

        BigDecimal p = BigDecimal.valueOf(previous);
        BigDecimal d = v.subtract(p);
        String trend = (d.signum() == 0) ? "same"
            : (lowerIsBetter ? (d.signum() < 0 ? "better" : "worse")
            : (d.signum() > 0 ? "better" : "worse"));
        return new GetSummaryResponse.Metric(key, title, unitName, v, p, d, trend);
    }

    private List<GetSummaryResponse.SeverityCount> calculateSeverities(
        String runId,
        RunEntity previous,
        List<String> subtree
    ) {
        Map<String, Long> current = toMap(issueRepository.countBySeverityForUnits(runId, subtree));
        Map<String, Long> prev = (previous == null)
            ? Map.of()
            : toMap(issueRepository.countBySeverityForUnits(previous.getId(), subtree));

        List<GetSummaryResponse.SeverityCount> result = new ArrayList<>();
        for (Severity s : Severity.values()) {
            long currentCount = current.getOrDefault(s.name(), 0L);
            long previousCount = prev.getOrDefault(s.name(), 0L);
            Long delta = (previous == null) ? null : currentCount - previousCount;
            result.add(
                new GetSummaryResponse.SeverityCount(
                    s.name().toLowerCase(),
                    (int) currentCount,
                    (previous == null) ? null : (int) previousCount,
                    (delta == null) ? null : delta.intValue()
                )
            );
        }
        return result;
    }

    private Map<String, Long> toMap(List<SeverityCountView> rows) {
        Map<String, Long> m = new HashMap<>();
        for (SeverityCountView v : rows) {
            m.put(v.getSeverity(), v.getCount());
        }
        return m;
    }
}