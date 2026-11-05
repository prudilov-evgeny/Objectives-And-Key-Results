package ru.cohenrol.objectivesandkeyresults.domain.model.response;

import java.time.LocalDate;
import java.util.List;

public record GetRunsResponse(
    List<Run> runs
) {
    public record Run(
        String id,
        LocalDate snapshotDate,
        String status,
        String comparedToRunId,
        int issuesCount
    ) {
    }
}