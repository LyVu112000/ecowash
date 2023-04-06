package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.ProductGroup;

import java.util.List;
import java.util.Optional;

public interface IProductGroupRepository extends BaseJpaRepository<ProductGroup, Long>, JpaSpecificationExecutor<ProductGroup> {
    Optional<ProductGroup> findByCodeOrNameAndTenantId(String code, String name, long tenantId);
    List<ProductGroup> findByNameAndTenantIdAndIdNot(String name, long tenantId, long id);
    Boolean existsByIdAndActiveAndTenantId(Long id, boolean active, long tenantId);

}