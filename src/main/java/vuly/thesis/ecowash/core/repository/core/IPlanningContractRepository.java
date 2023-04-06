package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.PlanningContract;

@Repository
public interface IPlanningContractRepository extends BaseJpaRepository<PlanningContract, Long>, JpaSpecificationExecutor<PlanningContract> {

}
