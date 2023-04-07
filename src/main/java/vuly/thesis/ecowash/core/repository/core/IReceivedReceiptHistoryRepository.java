package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.ReceivedReceiptHistory;

import java.util.List;

@Repository
public interface IReceivedReceiptHistoryRepository extends BaseJpaRepository<ReceivedReceiptHistory, Long>, JpaSpecificationExecutor<ReceivedReceiptHistory> {
    List<ReceivedReceiptHistory> findByReceivedId(Long id);
}