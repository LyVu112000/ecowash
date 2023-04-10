package vuly.thesis.ecowash.core.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningCreateRequest {
    private String code;
    private String name;
    private Instant fromDate;
    private Instant toDate;
    private List<PlanningContractCreateRequest> planningContracts = new ArrayList<>();
}
