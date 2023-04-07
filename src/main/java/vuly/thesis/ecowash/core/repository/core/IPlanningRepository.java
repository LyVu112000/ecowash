package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.Planning;

import java.util.Optional;

@Repository
public interface IPlanningRepository extends BaseJpaRepository<Planning, Long>, JpaSpecificationExecutor<Planning>, QuerydslPredicateExecutor<Planning> {

    @Query("SELECT MAX(sequenceNumber) FROM Planning")
    Optional<Integer> findMaxSequenceNumberByTenantId();
}
