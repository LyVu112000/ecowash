package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.ProductItem;

import java.util.List;
import java.util.Optional;

public interface IProductItemRepository extends BaseJpaRepository<ProductItem,Long>, JpaSpecificationExecutor<ProductItem> {
    Optional<ProductItem> findByCodeOrNameAndTenantId(String code, String name, long tenantId);
    List<ProductItem> findByNameAndTenantIdAndIdNot(String name, long tenantId, long id);
    Optional<ProductItem> findByIdAndTenantId(long id, long tenantId);
    List<ProductItem> findByActiveAndTenantId(boolean active, long tenantId);
    List<ProductItem> findByIsOtherAndTenantId(boolean isOther, long tenantId);
}