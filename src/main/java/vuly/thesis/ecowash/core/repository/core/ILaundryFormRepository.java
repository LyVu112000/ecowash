package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.LaundryForm;

import java.util.List;
import java.util.Optional;

public interface ILaundryFormRepository extends BaseJpaRepository<LaundryForm,Long>, JpaSpecificationExecutor<LaundryForm> {
    Optional<LaundryForm> findByValueAndTenantId(String value, long tenantId);
    List<LaundryForm> findByTenantIdOrderByIdAsc(long tenantId);
}