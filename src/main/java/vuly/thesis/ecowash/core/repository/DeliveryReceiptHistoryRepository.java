package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.DeliveryReceiptHistory;
import vuly.thesis.ecowash.core.repository.core.IDeliveryReceiptHistoryRepository;

import java.util.List;

@Service
public class DeliveryReceiptHistoryRepository extends BaseRepository<DeliveryReceiptHistory,Long, IDeliveryReceiptHistoryRepository> {
    @Autowired
    public DeliveryReceiptHistoryRepository(IDeliveryReceiptHistoryRepository repository) {
        super(repository);
    }


    public List<DeliveryReceiptHistory> findHistoryById(Long deliveryId) {
        return repository.findByDeliveryId(deliveryId);
    }
}