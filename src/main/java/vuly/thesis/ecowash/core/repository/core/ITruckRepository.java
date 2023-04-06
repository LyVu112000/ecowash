package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.Truck;
import vuly.thesis.ecowash.core.entity.type.Status;

import java.util.Optional;

@Repository
public interface ITruckRepository extends BaseJpaRepository<Truck,Long>, JpaSpecificationExecutor<Truck> {

    @Query(value = "SELECT * FROM truck s WHERE s.code = :code AND s.tenant_id = :tenantId AND s.deleted = false ORDER BY s.id DESC LIMIT 1", nativeQuery = true)
    Optional<Truck> findDuplicatedData(@Param("code") String truckCode, @Param("tenantId") long tenantId);

    Optional<Truck> findByIdAndStatusAndTenantId(Long id, Status status, Long tenantId);
}