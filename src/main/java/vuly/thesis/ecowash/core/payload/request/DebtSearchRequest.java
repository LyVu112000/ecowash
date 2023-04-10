package vuly.thesis.ecowash.core.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DebtSearchRequest {
    private Long customerId;
    private String productType;
    private Instant fromDate;
    private Instant toDate;
    private String specialInstructions;
    private String productItemIds;
    private String laundryFormValue;
    private Boolean isDelivery;
    private String receivedReceiptIds;
    private Boolean isRewash;
}
