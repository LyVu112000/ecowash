package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ReceivedReceiptHistory;
import vuly.thesis.ecowash.core.repository.core.IReceivedReceiptHistoryRepository;

import java.util.List;

@Service
public class ReceivedReceiptHistoryRepository extends BaseRepository<ReceivedReceiptHistory,Long, IReceivedReceiptHistoryRepository> {
    @Autowired
    public ReceivedReceiptHistoryRepository(IReceivedReceiptHistoryRepository repository) {
        super(repository);
    }

    public List<ReceivedReceiptHistory> findHistoryById(Long receivedId) {
        return repository.findByReceivedId(receivedId);
    }
}