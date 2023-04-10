package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDeliveryReceiptDto extends BaseDto{
    private String productItemName;
    private int numberReceived;
    private int numberDeliveryActual;
    private String laundryForm;
    private String note;
    private String pieceTypeValue;
}
