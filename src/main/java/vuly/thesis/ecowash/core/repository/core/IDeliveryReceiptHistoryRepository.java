package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.DeliveryReceiptHistory;

import java.util.List;

@Repository
public interface IDeliveryReceiptHistoryRepository extends BaseJpaRepository<DeliveryReceiptHistory, Long>, JpaSpecificationExecutor<DeliveryReceiptHistory> {
    List<DeliveryReceiptHistory> findByDeliveryIdAndTenantId(Long id, Long tenantId);
}