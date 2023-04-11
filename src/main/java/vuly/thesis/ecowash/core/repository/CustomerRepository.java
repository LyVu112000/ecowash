package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Customer;
import vuly.thesis.ecowash.core.repository.core.ICustomerRepository;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerRepository extends BaseRepository<Customer,Long, ICustomerRepository> {
    @Autowired
    public CustomerRepository(ICustomerRepository repository) {
        super(repository);
    }

    public Boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    public Optional<Customer> getById(long id) {
        return repository.findById(id);
    }

    public List<Customer> findByIdIn(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    public Customer findByIdAndActiveIsTrue(long id) {
        return repository.findByIdAndActiveIsTrue(id);
    }
}