package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.ProductItem;

import java.util.List;
import java.util.Optional;

public interface IProductItemRepository extends BaseJpaRepository<ProductItem,Long>, JpaSpecificationExecutor<ProductItem> {
    Optional<ProductItem> findByCodeOrName(String code, String name);
    List<ProductItem> findByNameAndIdNot(String name, long id);
    Optional<ProductItem> findById(long id);
    List<ProductItem> findByActive(boolean active);
    List<ProductItem> findByIsOther(boolean isOther);
}