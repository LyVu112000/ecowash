package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.DeliveryReceipt;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;
import vuly.thesis.ecowash.core.payload.dto.ReceiptByDay;
import vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Repository
public interface IDeliveryReceiptRepository extends BaseJpaRepository<DeliveryReceipt, Long>, JpaSpecificationExecutor<DeliveryReceipt> {
    @Query(value = "select SUM(number_delivery) total_delivery from item_delivery where delivery_receipt_id = :receiptId AND tenant_id = :tenantId ", nativeQuery = true)
    Long getTotalDeliveryByReceiptId(@Param("receiptId") Long receiptId, @Param("tenantId") long tenantId);

    @Query(value = "SELECT MAX(c.sequence_number) FROM core.delivery_receipt c WHERE c.code like %:code% AND tenant_id = :tenantId ", nativeQuery = true)
    Integer findByLikeCodeAndMaxSequenceNumber(@Param("code") String code, @Param("tenantId") long tenantId);

    @Query(value = "select new vuly.thesis.ecowash.core.payload.dto.ReceiptByDay(day(deliveryDate), count(id), 'PG') " +
            "from DeliveryReceipt " +
            "where month(deliveryDate) = :month AND year(deliveryDate) = :year AND tenantId = :tenantId GROUP BY day(deliveryDate)")
    List<ReceiptByDay> getTotalReceiptByMonthAndYear(int month, int year, @Param("tenantId") long tenantId);

    @Query(value = "select COALESCE(SUM(number_delivery_actual),0) - t1.number_received " +
            "            from (select received_receipt_id, product_item_id, tenant_id, SUM(number_received) as number_received from item_received " +
            "            group by received_receipt_id, product_item_id, tenant_id) t1 " +
            "            left join item_delivery t2 on t1.product_item_id = t2.product_item_id " +
            "            where t1.received_receipt_id = :receiptId and t2.received_receipt_id = :receiptId and t1.product_item_id = :productItemId " +
            "            and t2.delivery_receipt_id in (select id from delivery_receipt where status = 'DONE') " +
            "            and t1.tenant_id = :tenantId " +
            "            group by t2.product_item_id " , nativeQuery = true)
    Long getTotalActualDeliveryByReceivedReceiptId(@Param("receiptId") Long receiptId, @Param("productItemId") Long productItemId, @Param("tenantId") long tenantId);

    @Query(value = "select new vuly.thesis.ecowash.core.payload.dto.BriefDataDto(id, code) " +
            "from DeliveryReceipt " +
            "where code like %:code%  AND (:status is null or  status IN (:status)) AND tenantId = :tenantId")
    List<BriefDataDto> findCodeLike(String code, String status, long tenantId, Pageable pageable);

    @Query(value = "SELECT count(*) " +
            "FROM core.delivery_receipt " +
            "where customer_id = :customerId AND (delivery_date BETWEEN :fromDate AND :toDate) " +
            "AND status in ('DELIVERY', 'DONE') and tenant_id = :tenantId ", nativeQuery = true)
    Integer getDeliveryReceiptNumberCurrentDay(@Param("customerId") Long customerId, @Param("fromDate") Instant fromDate,  @Param("toDate") Instant toDate, @Param("tenantId") long tenantId);

    @Query(value = "SELECT * " +
            "FROM core.delivery_receipt dr " +
            "left join delivery_link_received dlr on dlr.delivery_receipt_id = dr.id " +
            "left join received_receipt rr on rr.id = dlr.received_receipt_id " +
            "where dr.id = :id and rr.status = 'DONE' and dr.tenant_id = :tenantId" , nativeQuery = true)
    Optional<DeliveryReceipt> checkExistedReceivedReceiptDone(@Param("id") Long id, @Param("tenantId") long tenantId);

    @Query(value = "select new vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto(dr.status, count(dr.status)) " +
            "from DeliveryReceipt dr " +
            "where dr.tenantId = :tenantId GROUP BY dr.status")
    List<ReceiptSummaryListDto> getDeliveryReceiptSummary(@Param("tenantId") long tenantId);
}