package ru.cohenrol.objectivesandkeyresults.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.RunEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.repository.RunRepository;
import ru.cohenrol.objectivesandkeyresults.domain.service.RunService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@Profile("scheduler")
@RequiredArgsConstructor
public class Scheduler {
    private final RunService runService;
    private final RunRepository runRepository;

    @Scheduled(cron = "0 0 3 * * MON")
    public void weeklyRun() {
        LocalDate snapshot = LocalDate.now();
        String runId = "RUN-" + snapshot.format(DateTimeFormatter.BASIC_ISO_DATE);

        if (runRepository.existsById(runId)) {
            log.info("KKD run {} already exists, skipping", runId);
            return;
        }

        log.info("Starting scheduled(0 0 3 * * MON) run {}", runId);
        RunEntity runEntity = runService.run(runId, snapshot);
        log.info("Run {} finished, issues: {}", runEntity.getId(), runEntity.getIssuesCount());
    }
}