package ru.cohenrol.objectivesandkeyresults.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.GoalEntity;

import java.util.Collection;
import java.util.List;

public interface GoalRepository extends JpaRepository<GoalEntity, String> {

    @Query(
        value = """
                SELECT *
                FROM goals
                WHERE status = :status
            """,
        nativeQuery = true
    )
    List<GoalEntity> findAllByStatusNative(
        @Param("status") String status
    );

    @Query(
        value = """
                SELECT g.*
                FROM goals g
                WHERE g.status = 'ACTIVE'
                  AND g.unit_id IN (:unitIds)
            """,
        nativeQuery = true
    )
    List<GoalEntity> findGoalsByUnitIds(
        @Param("unitIds") Collection<String> unitIds
    );
}
