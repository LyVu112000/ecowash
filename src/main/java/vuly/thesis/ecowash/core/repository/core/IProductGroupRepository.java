package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.ProductGroup;

import java.util.List;
import java.util.Optional;

public interface IProductGroupRepository extends BaseJpaRepository<ProductGroup, Long>, JpaSpecificationExecutor<ProductGroup> {
    Optional<ProductGroup> findByCodeOrName(String code, String name);
    List<ProductGroup> findByNameAndIdNot(String name, long id);
    Boolean existsByIdAndActive(Long id, boolean active);

}