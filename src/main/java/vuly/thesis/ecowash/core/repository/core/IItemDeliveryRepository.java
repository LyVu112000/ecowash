package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.ItemDelivery;

@Repository
public interface IItemDeliveryRepository extends BaseJpaRepository<ItemDelivery,Long>, JpaSpecificationExecutor<ItemDelivery> {

}