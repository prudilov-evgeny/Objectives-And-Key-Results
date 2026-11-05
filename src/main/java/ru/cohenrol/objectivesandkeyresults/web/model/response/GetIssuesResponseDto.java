package ru.cohenrol.objectivesandkeyresults.web.model.response;

import java.util.List;

public record GetIssuesResponseDto(
    long total,
    int limit,
    int offset,
    List<IssueDto> items
) {
    public record IssueDto(
        String rule,
        String objectType,
        String objectId,
        String severity
    ) {
    }
}