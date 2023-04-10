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
public class ReceivedReceiptDto extends BaseDto{
    private String code;
    private String productType;
    private String customerName;
    private Instant receivedDate;
    private Instant deliveryDate;
    private boolean isRewash = false;
    private String status;
    private String specialInstructions;
    private String deliveryCode;
    private Long totalItem;
    private String referenceCode;
}
