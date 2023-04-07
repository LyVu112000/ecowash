package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.PieceType;

import java.util.List;
import java.util.Optional;

public interface IPieceTypeRepository extends BaseJpaRepository<PieceType,Long>, JpaSpecificationExecutor<PieceType> {
    Optional<PieceType> findByValue(String value);

    List<PieceType> findByOrderByIdAsc();
}
