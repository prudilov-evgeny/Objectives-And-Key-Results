package ru.cohenrol.objectivesandkeyresults.domain.model.response;

import ru.cohenrol.objectivesandkeyresults.domain.model.Delta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetSummaryResponse(
    Run run,
    Run comparedTo,
    Unit unit,
    List<Metric> metrics,
    List<SeverityCount> issuesBySeverity,
    Delta delta
) {
    public record Run(
        String id,
        LocalDate snapshotDate
    ) {
    }

    public record Unit(
        String id,
        String name
    ) {
    }

    public record Metric(
        String key,
        String title,
        String unit,
        BigDecimal value,
        BigDecimal previous,
        BigDecimal delta,
        String trend
    ) {
    }

    public record SeverityCount(
        String code,
        Integer current,
        Integer previous,
        Integer delta
    ) {
    }
}