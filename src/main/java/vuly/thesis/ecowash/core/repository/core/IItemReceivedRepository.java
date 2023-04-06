package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.ItemReceived;

import java.util.List;

@Repository
public interface IItemReceivedRepository extends BaseJpaRepository<ItemReceived,Long>, JpaSpecificationExecutor<ItemReceived> {

    @Query(value =
            "SELECT ir.product_item_id, " +
            "   ir.number_received - SUM(CASE " +
            "       WHEN itd.number_delivery_actual <> 0 THEN itd.number_delivery_actual " +
            "       ELSE itd.number_delivery " +
            "   END) AS total_delivery " +
            "FROM item_received ir " +
            "   LEFT JOIN item_delivery itd ON itd.product_item_id = ir.product_item_id " +
            "WHERE ir.tenant_id = ?1 AND ir.received_receipt_id IN(?2) AND itd.received_receipt_id in(?2) " +
            "GROUP BY ir.id", nativeQuery = true)
    List<Object[]> getTotalDeliverable(Long tenantId, List<Long> receivedReceiptId);
}