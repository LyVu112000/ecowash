package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ItemReceived;
import vuly.thesis.ecowash.core.repository.core.IItemReceivedRepository;

import java.util.List;


@Service
public class ItemReceivedRepository extends BaseRepository<ItemReceived,Long, IItemReceivedRepository> {
    @Autowired
    public ItemReceivedRepository(IItemReceivedRepository repository) {
        super(repository);
    }

    public List<Object[]> getTotalDeliverable(List<Long> receivedReceiptId) {
        return repository.getTotalDeliverable(receivedReceiptId);
    }
}