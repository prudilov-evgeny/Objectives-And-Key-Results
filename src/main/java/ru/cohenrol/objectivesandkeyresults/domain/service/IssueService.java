package ru.cohenrol.objectivesandkeyresults.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.IssueEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.*;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;
import ru.cohenrol.objectivesandkeyresults.domain.model.response.GetIssuesResponse;
import ru.cohenrol.objectivesandkeyresults.domain.rules.RuleEngine;
import ru.cohenrol.objectivesandkeyresults.domain.service.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final GoalRepository goalRepository;
    private final KeyResultRepository keyResultRepository;
    private final RunRepository runRepository;
    private final IssueRepository issueRepository;
    private final RuleRepository ruleRepository;
    private final RuleEngine ruleEngine;
    private final UnitRepository unitRepository;

    @Transactional
    public GetIssuesResponse getIssues(
        String runId, String unitId,
        String rule, Severity severity,
        int limit, int offset
    ) {
        if (!runRepository.existsById(runId)) {
            throw new NotFoundException("RUN_NOT_FOUND", "run not found: " + runId);
        }

        List<String> unitIds = null;
        if (unitId != null) {
            unitIds = unitRepository.findSubtreeOfUnitIds(unitId);
            if (unitIds.isEmpty()) {
                throw new NotFoundException("UNIT_NOT_FOUND", "unit not found: " + unitId);
            }
        }

        boolean unitFilterDisabled = unitIds == null;
        List<String> effectiveUnitIds = unitIds == null ? List.of() : unitIds;

        long total = issueRepository.countFiltered(runId, effectiveUnitIds, rule, severity, unitFilterDisabled);
        List<IssueEntity> rows = issueRepository.findFiltered(
            runId, effectiveUnitIds,
            rule, severity,
            unitFilterDisabled,
            limit, offset
        );

        return new GetIssuesResponse(total, limit, offset,
            rows.stream()
                .map(i -> new GetIssuesResponse.Issue(
                    i.getRule().getCode(),
                    i.getObjectType().name(),
                    i.getObjectId(),
                    i.getSeverity().name().toLowerCase()
                ))
                .toList()
        );
    }
}