package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.SpecialInstruction;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISpecialInstructionRepository extends BaseJpaRepository<SpecialInstruction,Long>, JpaSpecificationExecutor<SpecialInstruction> {
    Optional<SpecialInstruction> findByValue(String value);
    List<SpecialInstruction> findByOrderByIdAsc();

    @Query(value = "select si.value from special_instruction si " +
            "LEFT JOIN special_instruction_receipt sir ON sir.special_instruction_id = si.id " +
            "LEFT JOIN delivery_receipt dr ON dr.id = sir.delivery_receipt_id WHERE dr.id = :deliveryReceiptId", nativeQuery = true)
    List<String> findByDeliveryReceiptId (@Param("deliveryReceiptId") long deliveryReceiptId);

    @Query("SELECT name FROM SpecialInstruction WHERE value IN (?2)")
    List<String> findByValueIn(List<String> values);

    @Query("SELECT name FROM SpecialInstruction WHERE id IN (?2)")
    List<String> findByIdIn(List<Long> ids);
}