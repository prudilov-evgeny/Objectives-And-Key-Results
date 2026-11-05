package ru.cohenrol.objectivesandkeyresults.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.cohenrol.objectivesandkeyresults.domain.ServicesGate;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetIssuesResponseDto;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetRunsResponseDto;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetSummaryResponseDto;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {
    private final ServicesGate servicesGate;

    @GetMapping("/kkd/summary")
    public ResponseEntity<GetSummaryResponseDto> getSummary(
        @RequestParam("run_id") String runId,
        @RequestParam("unit_id") String unitId
    ) {
        GetSummaryResponseDto response = servicesGate.getSummary(runId, unitId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/kkd/issues")
    public ResponseEntity<GetIssuesResponseDto> getIssues(
        @RequestParam("run_id") @NotBlank String runId,
        @RequestParam(value = "unit_id", required = false) String unitId,
        @RequestParam(value = "rule", required = false) String rule,
        @RequestParam(value = "severity", required = false) Severity severity,
        @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(100) int limit,
        @RequestParam(value = "offset", defaultValue = "0") @Min(0) int offset) {

        GetIssuesResponseDto response = servicesGate.getIssues(runId, unitId, rule, severity, limit, offset);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/runs")
    public ResponseEntity<GetRunsResponseDto> getRuns() {
        GetRunsResponseDto response = servicesGate.getRuns();
        return ResponseEntity.ok(response);
    }
}