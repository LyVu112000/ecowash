package vuly.thesis.ecowash.core.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ReportGeneralSearchRequest {
    private Long customerId;
    private String productType;
    private Instant fromDate;
    private Instant toDate;
    private String specialInstructions;
    private Long productItemId;
    private String laundryFormValue;
    private Boolean isDelivery;
    private Long receivedReceiptId;
    private Boolean isRewash;
}
