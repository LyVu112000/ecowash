package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Truck;
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.repository.core.ITruckRepository;

import java.util.Optional;


@Service
public class TruckRepository extends BaseRepository<Truck,Long, ITruckRepository> {
    @Autowired
    public TruckRepository(ITruckRepository repository) {
        super(repository);
    }

    public Optional<Truck> findFirstByCodeAndDeleted(String truckCode) {
        return repository.findDuplicatedData(truckCode);
    }

    public Optional<Truck> findByIdAndActive(Long truckId) {
        return repository.findByIdAndStatus(truckId, Status.ACTIVE);
    }
}