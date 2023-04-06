package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.Contract;
import vuly.thesis.ecowash.core.entity.type.ContractStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface IContractRepository extends BaseJpaRepository<Contract,Long>, JpaSpecificationExecutor<Contract> {

        @Query(value = "SELECT MAX(c.sequence_number) FROM core.contract c WHERE c.code like %:code% AND tenant_id = :tenantId ", nativeQuery = true)
        Integer findByLikeCodeAndMaxSequenceNumber(@Param("code") String code, @Param("tenantId") long tenantId);
        List<Contract> findByIdInAndStatusAndTenantId(List<Long> ids, ContractStatus status, Long tenantId);
        @Query(value = "SELECT * FROM core.contract c WHERE c.customer_id = :customerId AND tenant_id = :tenantId " +
                "and c.status = 'APPROVED' and c.valid_date <= :now and c.expired_date >= :now ", nativeQuery = true)
        Optional<Contract> findByCustomerIdAndApprovedStatus(long customerId, long tenantId, Instant now);
        Optional<Contract> findByCustomerIdAndTenantIdAndStatus(long customerId, long tenantId, ContractStatus status);

}