package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.ProductType;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductTypeRepository extends BaseJpaRepository<ProductType,Long>, JpaSpecificationExecutor<ProductType> {
    Optional<ProductType> findByValue(String value);
    List<ProductType> findByOrderByIdAsc();
}