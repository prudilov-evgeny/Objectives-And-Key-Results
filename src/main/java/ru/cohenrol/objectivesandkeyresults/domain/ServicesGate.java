package ru.cohenrol.objectivesandkeyresults.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;
import ru.cohenrol.objectivesandkeyresults.domain.service.IssueService;
import ru.cohenrol.objectivesandkeyresults.domain.service.RunService;
import ru.cohenrol.objectivesandkeyresults.domain.service.SummaryService;
import ru.cohenrol.objectivesandkeyresults.web.mapper.WebMapper;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetIssuesResponseDto;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetRunsResponseDto;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetSummaryResponseDto;

@Component
@RequiredArgsConstructor
public class ServicesGate {
    private final SummaryService summaryService;
    private final RunService runService;
    private final IssueService issueService;
    private final WebMapper webMapper;

    public GetSummaryResponseDto getSummary(String runId, String unitId) {
        return webMapper.toDto(
            summaryService.getSummary(runId, unitId)
        );
    }

    public GetRunsResponseDto getRuns() {
        return webMapper.toDto(
            runService.getRuns()
        );
    }

    public GetIssuesResponseDto getIssues(
        String runId, String unitId,
        String rule, Severity severity,
        int limit, int offset
    ) {
        return webMapper.toDto(
            issueService.getIssues(runId, unitId, rule, severity, limit, offset)
        );
    }
}
