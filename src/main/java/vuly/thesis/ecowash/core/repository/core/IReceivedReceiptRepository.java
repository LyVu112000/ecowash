package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.ReceivedReceipt;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;
import vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto;

import java.time.Instant;
import java.util.List;

@Repository
public interface IReceivedReceiptRepository extends BaseJpaRepository<ReceivedReceipt, Long>, JpaSpecificationExecutor<ReceivedReceipt> {
    @Query(value = "SELECT MAX(c.sequence_number) FROM core.received_receipt c WHERE c.code like %:code% AND tenant_id = :tenantId ", nativeQuery = true)
    Integer findByLikeCodeAndMaxSequenceNumber(@Param("code") String code, @Param("tenantId") long tenantId);

    @Query(value = "select COUNT(*) from delivery_link_received t1 left join delivery_receipt t2 on t2.id = t1.delivery_receipt_id " +
            "where ((t2.status != 'DONE')  OR (t2.status = 'DONE' AND t2.is_flag_error = true)) AND t1.received_receipt_id = :receiptId AND t1.tenant_id = :tenantId ", nativeQuery = true)
    Integer findNumberDeliveryReceiptHasNotDone(@Param("receiptId") long receiptId, @Param("tenantId") long tenantId);

    @Query(value = "select new vuly.thesis.ecowash.core.payload.dto.BriefDataDto(id, code) " +
            "from ReceivedReceipt " +
            "where code like %:code% AND tenantId = :tenantId AND (:status is null or  status IN (:status))")
    List<BriefDataDto> findCodeLike(String code, String status, long tenantId, Pageable pageable);

//    @Query(value = "select new ebst.ecowash.core.payload.dto.ReceiptByDay(day(receivedDate), count(id), 'PN') " +
//            "from ReceivedReceipt " +
//            "where month(receivedDate) = :month AND year(receivedDate) = :year AND tenantId = :tenantId GROUP BY day(receivedDate)")
//    List<ReceiptByDay> getTotalReceiptByMonthAndYear(int month, int year, long tenantId);
//
//    @Query(value = "select new ebst.ecowash.core.payload.dto.ReceiptByDay(day(receivedDate), count(id), 'RW') " +
//            "from ReceivedReceipt " +
//            "where month(receivedDate) = :month AND year(receivedDate) = :year AND tenantId = :tenantId AND isRewash = true GROUP BY day(receivedDate)")
//    List<ReceiptByDay> getTotalRewashReceiptByMonthAndYear(int month, int year, long tenantId);

    @Query(value = "SELECT count(*) " +
            "FROM core.received_receipt " +
            "where customer_id = :customerId AND (received_date BETWEEN :fromDate AND :toDate) " +
            "AND status != 'CANCEL' and tenant_id = :tenantId ", nativeQuery = true)
    Integer getReceivedReceiptNumberCurrentDay(@Param("customerId") Long customerId, @Param("fromDate") Instant fromDate,  @Param("toDate") Instant toDate, @Param("tenantId") long tenantId);

    @Query(value = "select new vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto(rr.status, count(rr.status)) " +
            "from ReceivedReceipt rr " +
            "where rr.tenantId = :tenantId and rr.isRewash = false GROUP BY rr.status ")
    List<ReceiptSummaryListDto> getReceivedReceiptSummary(@Param("tenantId") long tenantId);

    @Query(value = "select new vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto(rr.status, count(rr.status)) " +
            "from ReceivedReceipt rr " +
            "where rr.tenantId = :tenantId and rr.isRewash = true GROUP BY rr.status ")
    List<ReceiptSummaryListDto> getRewashReceiptSummary(@Param("tenantId") long tenantId);

    @Query(value = "select new vuly.thesis.ecowash.core.payload.dto.BriefDataDto(id, code) " +
            "from ReceivedReceipt " +
            "where customer.id = :customerId AND tenantId = :tenantId AND status = 'PACKING' AND productType.id = :productTypeId" +
            " AND receivedDate between  :fromDate AND :toDate")
    List<BriefDataDto> findAllByForDebt(Long customerId, long tenantId, Instant fromDate, Instant toDate, Long productTypeId);
}