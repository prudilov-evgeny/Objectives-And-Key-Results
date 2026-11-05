package ru.cohenrol.objectivesandkeyresults.domain.model.response;

import java.util.List;

public record GetIssuesResponse(
    long total,
    int limit,
    int offset,
    List<Issue> items
) {
    public record Issue(
        String rule,
        String objectType,
        String objectId,
        String severity
    ) {
    }
}