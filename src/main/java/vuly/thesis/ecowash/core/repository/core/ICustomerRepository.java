package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.Customer;

import java.util.List;

@Repository
public interface ICustomerRepository extends BaseJpaRepository<Customer,Long>, JpaSpecificationExecutor<Customer> {

    Boolean existsByCode(String code);

    List<Customer> findByTenantIdAndIdIn(Long tenantId, List<Long> ids);
    Customer findByIdAndActiveIsTrue(long id);
}