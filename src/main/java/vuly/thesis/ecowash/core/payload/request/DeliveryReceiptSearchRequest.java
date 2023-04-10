package vuly.thesis.ecowash.core.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReceiptSearchRequest {
    private String code;
    private Long customerId;
    private String productTypeValue;
    private Instant fromDate;
    private Instant toDate;
    private Boolean isRewash;
//    private String specialInstruction;
    private String statuses;
    private Boolean isFlagError;
    private Boolean isDebt;
    private Long receivedId;
    private String customerCodeOrName;
}
