package vuly.thesis.ecowash.core.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningContractUpdateRequest {
    private Long id;
    private Long contractId;
    private Long customerId;
    private String timeReceived;
    private String timeDelivery;
    private Integer bagNumber;
    private Integer numberOfLooseBags;
    private String note;
    private String washingTeam;
    private String productTypeValue;
}
