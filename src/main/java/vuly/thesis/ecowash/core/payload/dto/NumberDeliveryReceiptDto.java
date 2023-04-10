package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumberDeliveryReceiptDto extends BaseDto{
    private Long receiptId;
    private Long productItemId;
    private String productItemValue;
    private String productItemName;
    private String pieceTypeValue;
    private String pieceTypeName;
    private Long laundryFormId;
    private String laundryFormName;
    private String laundryFormValue;
    private Integer numberDelivery;
    private Integer numberCanDelivery;
    private Integer numberReceived;
    private Integer randomNumberCheck;
    private Integer numberAfterProduction;
    private Integer numberDeliveryActual;
    private String note;
    private String receivedCode;
    private String numberRoom;

}
