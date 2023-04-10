package vuly.thesis.ecowash.core.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Contract;
import vuly.thesis.ecowash.core.entity.type.ContractStatus;
import vuly.thesis.ecowash.core.repository.core.IContractRepository;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
public class ContractRepository extends BaseRepository<Contract,Long, IContractRepository> {

    @Autowired
    public ContractRepository(IContractRepository repository) {
        super(repository);
    }

    @Autowired
    EbstUserRequest ebstUserRequest;

    public Optional<Contract> findById(Long id) {
        return repository.findById(id);
    }

    public Integer findByLikeCodeAndMaxSequenceNumber(String code) {
        return repository.findByLikeCodeAndMaxSequenceNumber(code);
    }

    public List<Contract> findByIdInAndStatus(List<Long> ids, ContractStatus status) {
        return repository.findByIdInAndStatus(ids, status);
    }
    public Optional<Contract> findByCustomerIdAndApprovedStatus(long customerId){
        return repository.findByCustomerIdAndApprovedStatus(customerId, Instant.now());
    }

    public Optional<Contract> findByCustomerIdAndStatus(long customerId){
        return repository.findByCustomerIdAndStatus(customerId, ContractStatus.APPROVED);
    }
 }