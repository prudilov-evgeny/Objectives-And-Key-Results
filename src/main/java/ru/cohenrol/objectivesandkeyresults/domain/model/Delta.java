package ru.cohenrol.objectivesandkeyresults.domain.model;

public record Delta(
    Integer newCount,
    Integer persisting,
    Integer resolved
) {
    public static Delta noComparison() {
        return new Delta(null, null, null);
    }
}