package ru.cohenrol.objectivesandkeyresults.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.KeyResultEntity;

import java.util.List;

public interface KeyResultRepository extends JpaRepository<KeyResultEntity, String> {

    @Query(
        value = """
                SELECT kr.*
                FROM key_results kr
                JOIN goals g ON g.id = kr.goal_id
                WHERE g.status = :status
            """,
        nativeQuery = true
    )
    List<KeyResultEntity> findAllByGoalStatusNative(
        @Param("status") String status
    );
}