package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.PaidBy;

import java.util.List;
import java.util.Optional;

public interface IPaidByRepository extends BaseJpaRepository<PaidBy,Long>, JpaSpecificationExecutor<PaidBy> {
    Optional<PaidBy> findByValue(String value);
    List<PaidBy> findByOrderByIdAsc();
}