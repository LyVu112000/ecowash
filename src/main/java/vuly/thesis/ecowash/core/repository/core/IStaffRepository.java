package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.Staff;
import vuly.thesis.ecowash.core.entity.type.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface IStaffRepository extends BaseJpaRepository<Staff,Long>, JpaSpecificationExecutor<Staff> {

    @Query(value = "SELECT * FROM staff s WHERE ((s.email = :email OR s.code = :code) AND s.tenant_id = :tenantId AND s.deleted = false) OR (s.username = :username AND s.tenant_id = :tenantId) ORDER BY s.id DESC LIMIT 1", nativeQuery = true)
    Optional<Staff> findDuplicatedData(@Param("email") String email, @Param("code") String staffCode, @Param("username") String username, @Param("tenantId") long tenantId);

    Optional<Staff> findByIdAndSignatureAndTenantId(long id, String signature, long tenantId);

    @Query("SELECT s.email FROM Staff s WHERE s.tenantId = :tenantId AND s.customerId = :customerId AND s.isCustomer = :isCustomer")
    List<String> findByTenantIdAndCustomerIdAAndIsCustomer(Long tenantId, Long customerId, Boolean isCustomer);

    Optional<Staff> findByIdAndStatusAndTenantId(Long id, Status status, Long tenantId);
}