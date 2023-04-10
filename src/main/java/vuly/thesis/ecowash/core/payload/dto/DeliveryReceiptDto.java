package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReceiptDto extends BaseDto{
    private String code;
    private String productType;
    private String customerName;
    private Instant deliveryDate;
    private String status;
    private String customerCode;
//    private String specialInstructions;
//    private List<String> specialInstructionsList;
    private int numberOfLooseBags;
    private int bagNumber;
    private long weight;
    private String staffCheck;
    private Instant checkDate;
    private String numberRoom;
    private String note;
    private Long customerId;
    private String productTypeValue;
    private Instant receivedDate;
    private List<Long> receivedIds;
    private List<ItemDeliveryDto> itemDeliveryList;
    private String receivedCode;
    private Long totalItem;
    private List<ReceivedReceiptDto> receiptDtos;

    private String deliveryCode;
    private boolean isExpress;
    private Boolean isDebt;

}
