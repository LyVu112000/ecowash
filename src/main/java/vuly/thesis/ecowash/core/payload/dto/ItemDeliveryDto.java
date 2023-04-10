package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDeliveryDto extends BaseDto{


    private Long receivedReceiptId;
    private long productItemId;
    private String productItemName;
    private String productItemCode;
    private String pieceTypeName;
    private Integer numberReceived;
    private String laundryFormName;
    private String laundryFormValue;
    private Long laundryFormId;
    private String note;
    private Integer randomNumberCheck;
    private Integer numberAfterProduction;
    private Integer numberDelivery;
    private Integer numberDeliveryActual;
    private Instant receivedDate;

    private Long deliveryReceiptId;
}
