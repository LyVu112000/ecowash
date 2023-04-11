package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ReceivedReceipt;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;
import vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto;
import vuly.thesis.ecowash.core.repository.core.IReceivedReceiptRepository;

import java.time.Instant;
import java.util.List;


@Service
public class ReceivedReceiptRepository extends BaseRepository<ReceivedReceipt,Long, IReceivedReceiptRepository> {
    @Autowired
    public ReceivedReceiptRepository(IReceivedReceiptRepository repository) {
        super(repository);
    }

    public Integer findByLikeCodeAndMaxSequenceNumber(String code) {
        return repository.findByLikeCodeAndMaxSequenceNumber(code);
    }

    public Integer findNumberDeliveryReceiptHasNotDone(long receiptId) {
        return repository.findNumberDeliveryReceiptHasNotDone(receiptId);
    }

    public List<BriefDataDto> findCodeLike(String code, String status, Pageable pageable){
        return repository.findCodeLike(code, status, pageable);
    }

    public Integer getReceivedReceiptNumberCurrentDay(Long customerId, Instant fromDate, Instant toDate) {
        return repository.getReceivedReceiptNumberCurrentDay(customerId, fromDate, toDate);
    }

    public List<ReceiptSummaryListDto> getReceivedReceiptSummary() {
        return repository.getReceivedReceiptSummary();
    }
    public List<ReceiptSummaryListDto> getRewashReceiptSummary() {
        return repository.getRewashReceiptSummary();
    }

    public List<BriefDataDto> findAllByForDebt(Long customerId, Instant fromDate, Instant toDate, Long productTypeId){
        return repository.findAllByForDebt(customerId, fromDate, toDate, productTypeId);
    }
}