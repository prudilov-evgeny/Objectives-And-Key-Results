package ru.cohenrol.objectivesandkeyresults.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.RunEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RunRepository extends JpaRepository<RunEntity, String> {
    @Query(
        value = """
                SELECT * FROM runs
                WHERE snapshot_date < :date
                ORDER BY snapshot_date DESC
                LIMIT 1
            """,
        nativeQuery = true
    )
    Optional<RunEntity> findPreviousBefore(@Param("date") LocalDate date);

    @Modifying
    @Query(
        value = """
                INSERT INTO runs (id, snapshot_date, status, compared_to_run_id, issues_count)
                VALUES (:id, :snapshotDate, :status, :comparedToId, :issuesCount)
            """,
        nativeQuery = true
    )
    void insertRun(
        @Param("id") String id,
        @Param("snapshotDate") LocalDate snapshotDate,
        @Param("status") String status,
        @Param("comparedToId") String comparedToId,
        @Param("issuesCount") int issuesCount
    );

    @Query(
        value = """
                SELECT *
                FROM runs
                WHERE id = :id
            """,
        nativeQuery = true
    )
    Optional<RunEntity> findRunById(@Param("id") String id);

    @Query(
        value = """
                SELECT * FROM runs ORDER BY snapshot_date DESC
            """,
        nativeQuery = true
    )
    List<RunEntity> findAllOrdered();
}