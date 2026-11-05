package ru.cohenrol.objectivesandkeyresults.web.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.cohenrol.objectivesandkeyresults.domain.model.Delta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetSummaryResponseDto(
    RunRef run,
    RunRef comparedTo,
    UnitRef unit,
    List<Metric> metrics,
    List<SeverityCount> issuesBySeverity,
    Delta delta
) {
    public record RunRef(
        String id, @JsonProperty("snapshot_date") LocalDate snapshotDate
    ) {
    }

    public record UnitRef(String id, String name) {
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