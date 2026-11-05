package ru.cohenrol.objectivesandkeyresults.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.RunEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.*;
import ru.cohenrol.objectivesandkeyresults.domain.model.FoundIssue;
import ru.cohenrol.objectivesandkeyresults.domain.model.response.GetRunsResponse;
import ru.cohenrol.objectivesandkeyresults.domain.rules.RuleEngine;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RunService {

    private final GoalRepository goalRepository;
    private final KeyResultRepository keyResultRepository;
    private final RunRepository runRepository;
    private final IssueRepository issueRepository;
    private final RuleRepository ruleRepository;
    private final RuleEngine ruleEngine;

    @Transactional
    public GetRunsResponse getRuns() {
        return new GetRunsResponse(
            runRepository.findAllOrdered().stream()
                .map(r -> new GetRunsResponse.Run(
                    r.getId(),
                    r.getSnapshotDate(),
                    r.getStatus().name(),
                    r.getComparedTo() == null ? null : r.getComparedTo().getId(),
                    r.getIssuesCount()
                ))
                .toList()
        );
    }

    @Transactional
    public RunEntity run(String runId, LocalDate snapshotDate) {
        List<GoalEntity> goalEntities = goalRepository.findAllByStatusNative("ACTIVE");
        List<KeyResultEntity> keyResultEntities = keyResultRepository.findAllByGoalStatusNative("ACTIVE");

        List<FoundIssue> found = ruleEngine.run(goalEntities, keyResultEntities, snapshotDate);

        RunEntity previous = runRepository.findPreviousBefore(snapshotDate).orElse(null);
        String comparedToId = previous != null ? previous.getId() : null;

        runRepository.insertRun(
            runId,
            snapshotDate,
            "DONE",
            comparedToId,
            found.size()
        );

        for (FoundIssue issue : found) {
            issueRepository.insertIssue(
                runId,
                issue.ruleCode().name(),
                issue.objectType().name(),
                issue.objectId(),
                issue.severity().name()
            );
        }

        return runRepository.findRunById(runId).orElseThrow();
    }
}