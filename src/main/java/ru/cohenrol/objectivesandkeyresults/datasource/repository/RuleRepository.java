package ru.cohenrol.objectivesandkeyresults.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.cohenrol.objectivesandkeyresults.datasource.entity.RuleEntity;

public interface RuleRepository extends JpaRepository<RuleEntity, String> {
}