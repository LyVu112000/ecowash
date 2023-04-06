package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.PieceType;

import java.util.List;
import java.util.Optional;

public interface IPieceTypeRepository extends BaseJpaRepository<PieceType,Long>, JpaSpecificationExecutor<PieceType> {
    Optional<PieceType> findByValueAndTenantId(String value, long tenantId);
    List<PieceType> findByTenantIdOrderByIdAsc(long tenantId);
}