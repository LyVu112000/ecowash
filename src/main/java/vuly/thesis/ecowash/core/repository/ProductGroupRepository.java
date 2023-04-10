package vuly.thesis.ecowash.core.repository;

import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ProductGroup;
import vuly.thesis.ecowash.core.repository.core.IProductGroupRepository;


@Service
public class ProductGroupRepository extends BaseRepository<ProductGroup, Long, IProductGroupRepository> {

    public ProductGroupRepository(IProductGroupRepository repository) {
        super(repository);
    }

    public boolean checkExistedProductGroup(long id, boolean active) {
        return repository.existsByIdAndActive(id, active);
    }
}
