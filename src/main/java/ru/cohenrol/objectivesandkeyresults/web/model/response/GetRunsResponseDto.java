package ru.cohenrol.objectivesandkeyresults.web.model.response;

import java.time.LocalDate;
import java.util.List;

public record GetRunsResponseDto(
    List<RunDto> runs
) {
    public record RunDto(
        String id,
        LocalDate snapshotDate,
        String status,
        String comparedToRunId,
        int issuesCount
    ) {
    }
}