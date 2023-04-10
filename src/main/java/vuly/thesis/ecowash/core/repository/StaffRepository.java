package vuly.thesis.ecowash.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Staff;
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.repository.core.IStaffRepository;

import java.util.List;
import java.util.Optional;


@Service
public class StaffRepository extends BaseRepository<Staff,Long, IStaffRepository> {
    @Autowired
    public StaffRepository(IStaffRepository repository) {
        super(repository);
    }

    public Optional<Staff> findFirstByEmailOrCodeOrUsernameAndDeleted(String email, String staffCode, String username) {
        return repository.findDuplicatedData(email, staffCode, username);
    }

    public Optional<Staff> findByIdAndExistedSignature(long id, String signature) {
        return repository.findByIdAndSignatureAndTenantId(id, signature);
    }

    public List<String> findByTenantIdAndCustomerIdAAndIsCustomer(Long customerId, Boolean isCustomer) {
        return repository.findByTenantIdAndCustomerIdAAndIsCustomer(customerId, isCustomer);
    }

    public Optional<Staff> findByIdAndActive(long id) {
        return repository.findByIdAndStatusAndTenantId(id, Status.ACTIVE);
    }
}