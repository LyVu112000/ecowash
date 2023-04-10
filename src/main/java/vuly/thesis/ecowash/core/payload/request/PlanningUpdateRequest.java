package vuly.thesis.ecowash.core.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vuly.thesis.ecowash.core.entity.type.PlanningStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningUpdateRequest {
    private String name;
    private Instant fromDate;
    private Instant toDate;
    private PlanningStatus status;
    private List<PlanningContractUpdateRequest> planningContracts = new ArrayList<>();
}
