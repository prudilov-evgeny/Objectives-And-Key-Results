package ru.cohenrol.objectivesandkeyresults.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.IssueEntity;
import ru.cohenrol.objectivesandkeyresults.datasource.view.IssueKeyView;
import ru.cohenrol.objectivesandkeyresults.datasource.view.SeverityCountView;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;

import java.util.Collection;
import java.util.List;

public interface IssueRepository extends JpaRepository<IssueEntity, Long> {

    @Query(
        value = """
                SELECT i.*
                FROM issues i
                WHERE i.run_id = CAST(:runId AS VARCHAR)
                  AND (CAST(:rule AS VARCHAR) IS NULL OR i.rule_code = CAST(:rule AS VARCHAR))
                  AND (CAST(:severity AS VARCHAR) IS NULL OR i.severity = CAST(:severity AS VARCHAR))
                  AND (CAST(:unitFilterDisabled AS BOOLEAN) = TRUE
                       OR (i.object_type = 'GOAL' AND i.object_id IN (
                               SELECT g.id FROM goals g WHERE g.unit_id IN (:unitIds)
                           ))
                       OR (i.object_type = 'KR' AND i.object_id IN (
                               SELECT kr.id FROM key_results kr
                               JOIN goals g ON g.id = kr.goal_id
                               WHERE g.unit_id IN (:unitIds)
                           )))
                ORDER BY i.severity, i.id
                LIMIT :limit OFFSET :offset
        """,
        nativeQuery = true
    )
    List<IssueEntity> findFiltered(
        @Param("runId") String runId,
        @Param("unitIds") Collection<String> unitIds,
        @Param("rule") String rule,
        @Param("severity") String severity,
        @Param("unitFilterDisabled") boolean unitFilterDisabled,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    @Query(
        value = """
                SELECT COUNT(*)
                FROM issues i
                WHERE i.run_id = CAST(:runId AS VARCHAR)
                  AND (CAST(:rule AS VARCHAR) IS NULL OR i.rule_code = CAST(:rule AS VARCHAR))
                  AND (CAST(:severity AS VARCHAR) IS NULL OR i.severity = CAST(:severity AS VARCHAR))
                  AND (CAST(:unitFilterDisabled AS BOOLEAN) = TRUE
                       OR (i.object_type = 'GOAL' AND i.object_id IN (
                               SELECT g.id FROM goals g WHERE g.unit_id IN (:unitIds)
                           ))
                       OR (i.object_type = 'KR' AND i.object_id IN (
                               SELECT kr.id FROM key_results kr
                               JOIN goals g ON g.id = kr.goal_id
                               WHERE g.unit_id IN (:unitIds)
                           )))
            """,
        nativeQuery = true
    )
    long countFiltered(
        @Param("runId") String runId,
        @Param("unitIds") Collection<String> unitIds,
        @Param("rule") String rule,
        @Param("severity") String severity,
        @Param("unitFilterDisabled") boolean unitFilterDisabled
    );

    @Query(
        value = """
                SELECT i.rule_code AS ruleCode, i.object_id AS objectId
                FROM issues i WHERE i.run_id = :runId
            """,
        nativeQuery = true
    )
    List<IssueKeyView> findIssueKeysByRunId(
        @Param("runId") String runId
    );


    @Query(
        value = """
                SELECT i.*
                FROM issues i
                WHERE i.run_id = :runId
                  AND (
                    (i.object_type = 'GOAL' AND i.object_id IN (
                        SELECT g.id FROM goals g WHERE g.unit_id IN (:unitIds)
                    ))
                    OR
                    (i.object_type = 'KR' AND i.object_id IN (
                        SELECT kr.id FROM key_results kr
                        JOIN goals g ON g.id = kr.goal_id
                        WHERE g.unit_id IN (:unitIds)
                    ))
                  )
            """,
        nativeQuery = true
    )
    List<IssueEntity> findIssuesByRunAndUnitIds(
        @Param("runId") String runId,
        @Param("unitIds") Collection<String> unitIds
    );

    @Query(
        value = """
                SELECT i.severity AS severity, COUNT(*) AS count
                FROM issues i
                WHERE i.run_id = :runId
                  AND (
                    (i.object_type = 'GOAL' AND i.object_id IN (
                        SELECT g.id FROM goals g WHERE g.unit_id IN (:unitIds)
                    ))
                    OR
                    (i.object_type = 'KR' AND i.object_id IN (
                        SELECT kr.id FROM key_results kr
                        JOIN goals g ON g.id = kr.goal_id
                        WHERE g.unit_id IN (:unitIds)
                    ))
                  )
                GROUP BY i.severity
            """,
        nativeQuery = true
    )
    List<SeverityCountView> countBySeverityForUnits(
        @Param("runId") String runId,
        @Param("unitIds") Collection<String> unitIds
    );

    @Modifying
    @Query(
        value = """
                INSERT INTO issues (run_id, rule_code, object_type, object_id, severity)
                VALUES (:runId, :ruleCode, :objectType, :objectId, :severity)
            """,
        nativeQuery = true
    )
    void insertIssue(
        @Param("runId") String runId,
        @Param("ruleCode") String ruleCode,
        @Param("objectType") String objectType,
        @Param("objectId") String objectId,
        @Param("severity") String severity
    );
}