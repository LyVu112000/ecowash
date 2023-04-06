package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EnumData {
    List<ProductType> productTypes;
    List<SpecialInstruction> specialInstructions;
    List<LaundryForm> laundryForms;
    List<PieceType> pieceTypes;
    List<PaidBy> paidBys;
}
