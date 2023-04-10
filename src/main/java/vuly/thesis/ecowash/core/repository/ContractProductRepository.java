package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ContractProduct;
import vuly.thesis.ecowash.core.repository.core.IContractProductRepository;

@Service
public class ContractProductRepository extends BaseRepository<ContractProduct,Long, IContractProductRepository> {
    @Autowired
    public ContractProductRepository(IContractProductRepository repository) {
        super(repository);
    }
}