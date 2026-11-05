package ru.cohenrol.objectivesandkeyresults.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.UnitEntity;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<UnitEntity, String> {

    @Query(
        value = """
                WITH RECURSIVE subtree AS (
                    SELECT id FROM units WHERE id = :unitId
                    UNION ALL
                    SELECT u.id FROM units u
                    JOIN subtree s ON u.parent_id = s.id
                )
                SELECT id FROM subtree
            """,
        nativeQuery = true
    )
    List<String> findSubtreeOfUnitIds(
        @Param("unitId") String unitId
    );

    @Query(
        value = """
            SELECT * FROM units WHERE id = :id
        """,
        nativeQuery = true
    )
    Optional<UnitEntity> findUnitById(
        @Param("id") String id
    );
}