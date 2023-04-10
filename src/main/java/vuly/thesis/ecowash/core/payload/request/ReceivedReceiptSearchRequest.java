package vuly.thesis.ecowash.core.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedReceiptSearchRequest {
    private String code;
    private String customerCodeOrName;
    private Long customerId;
    private String productTypeValue;
    private Instant fromDate;
    private Instant toDate;
    private Boolean isRewash;
    private String specialInstruction;
    private String status;
    private Boolean isFlagError;
    private List<Long> receivedIds;
    private Long deliveryId;
}
