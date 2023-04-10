package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ItemDelivery;
import vuly.thesis.ecowash.core.repository.core.IItemDeliveryRepository;


@Service
public class ItemDeliveryRepository extends BaseRepository<ItemDelivery,Long, IItemDeliveryRepository> {
    @Autowired
    public ItemDeliveryRepository(IItemDeliveryRepository repository) {
        super(repository);
    }
}