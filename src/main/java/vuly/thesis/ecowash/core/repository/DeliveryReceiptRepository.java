package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.DeliveryReceipt;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;
import vuly.thesis.ecowash.core.payload.dto.ReceiptByDay;
import vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto;
import vuly.thesis.ecowash.core.repository.core.IDeliveryReceiptRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
public class DeliveryReceiptRepository extends BaseRepository<DeliveryReceipt,Long, IDeliveryReceiptRepository> {
    @Autowired
    public DeliveryReceiptRepository(IDeliveryReceiptRepository repository) {
        super(repository);
    }

    public Integer findByLikeCodeAndMaxSequenceNumber(String code) {
        return repository.findByLikeCodeAndMaxSequenceNumber(code);
    }

    public Long getTotalDeliveryByReceiptId(Long id) {
        return repository.getTotalDeliveryByReceiptId(id);
    }

    public List<ReceiptByDay> receipt(int month, int year){
        return repository.getTotalReceiptByMonthAndYear(month, year);
    }

    public Long getTotalActualDeliveryByReceivedReceiptId(Long id, Long productItemId) {
        return repository.getTotalActualDeliveryByReceivedReceiptId(id, productItemId);
    }

    public List<BriefDataDto> findCodeLike(String code, String status, Pageable pageable){
        return repository.findCodeLike(code, status, pageable);
    }

    public Integer getDeliveryReceiptNumberCurrentDay(Long customerId, Instant fromDate, Instant toDate) {
        return repository.getDeliveryReceiptNumberCurrentDay(customerId, fromDate, toDate);
    }

    public Optional<DeliveryReceipt> checkExistedReceivedReceiptDone(Long id) {
        return repository.checkExistedReceivedReceiptDone(id);
    }

    public List<ReceiptSummaryListDto> getDeliveryReceiptSummary() {
        return repository.getDeliveryReceiptSummary();
    }
}