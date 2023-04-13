package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Planning;
import vuly.thesis.ecowash.core.repository.core.IPlanningRepository;

import java.util.Optional;

@Service
public class PlanningRepository extends BaseRepository<Planning, Long, IPlanningRepository> {

    @Autowired
    public PlanningRepository(IPlanningRepository repository) {
        super(repository);
    }

    public Optional<Integer> findMaxSequenceNumber() {
        return repository.findMaxSequenceNumber();
    }
}
