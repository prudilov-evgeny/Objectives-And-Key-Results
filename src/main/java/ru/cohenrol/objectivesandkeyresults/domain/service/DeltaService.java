package ru.cohenrol.objectivesandkeyresults.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.IssueRepository;
import ru.cohenrol.objectivesandkeyresults.domain.model.Delta;
import ru.cohenrol.objectivesandkeyresults.domain.model.IssueKey;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeltaService {
    private final IssueRepository issueRepository;

    private Set<IssueKey> getIssueKeys(String runId) {
        return issueRepository.findIssueKeysByRunId(runId).stream()
            .map(v -> new IssueKey(v.getRuleCode(), v.getObjectId()))
            .collect(Collectors.toSet());
    }

    public Delta calculate(String currentRunId, String previousRunId) {
        if (previousRunId == null) {
            return Delta.noComparison();
        }

        Set<IssueKey> current = getIssueKeys(currentRunId);
        Set<IssueKey> previous = getIssueKeys(previousRunId);

        long newCount = current.stream().filter(k -> previous.contains(k) == false).count();
        long persisting = current.stream().filter(previous::contains).count();
        long resolved = previous.stream().filter(k -> current.contains(k) == false).count();

        return new Delta((int) newCount, (int) persisting, (int) resolved);
    }
}