package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.ContractProduct;

@Repository
public interface IContractProductRepository extends BaseJpaRepository<ContractProduct,Long>, JpaSpecificationExecutor<ContractProduct> {

}