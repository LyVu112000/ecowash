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

        @Query(value = "SELECT MAX(c.sequence_number) FROM core.contract c WHERE c.code like %:code%   ", nativeQuery = true)
        Integer findByLikeCodeAndMaxSequenceNumber(@Param("code") String code);
        List<Contract> findByIdInAndStatus(List<Long> ids, ContractStatus status);
        @Query(value = "SELECT * FROM core.contract c WHERE c.customer_id = :customerId   " +
                "and c.status = 'APPROVED' and c.valid_date <= :now and c.expired_date >= :now ", nativeQuery = true)
        Optional<Contract> findByCustomerIdAndApprovedStatus(long customerId, Instant now);
        Optional<Contract> findByCustomerIdAndStatus(long customerId, ContractStatus status);

}