package ru.cohenrol.objectivesandkeyresults.domain.model;

import ru.cohenrol.objectivesandkeyresults.domain.enums.ObjectType;
import ru.cohenrol.objectivesandkeyresults.domain.enums.RuleCode;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;

public record FoundIssue(
    RuleCode ruleCode,
    ObjectType objectType,
    String objectId,
    Severity severity
) {
}