package vuly.thesis.ecowash.core.repository;

import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ProductType;
import vuly.thesis.ecowash.core.repository.core.IProductTypeRepository;

import java.util.Optional;

@Service
public class ProductTypeRepository extends BaseRepository<ProductType, Long, IProductTypeRepository> {

    public ProductTypeRepository(IProductTypeRepository repository) {
        super(repository);
    }

    public Optional<ProductType> findByValue(String value) {
        return repository.findByValue(value);
    }
}
