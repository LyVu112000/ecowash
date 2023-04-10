package vuly.thesis.ecowash.core.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vuly.thesis.ecowash.core.entity.type.PlanningStatus;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanningSearchRequest {
    private String codeOrName;
    private String customerFullName;
    private PlanningStatus status;
    private Instant fromDate;
    private Instant toDate;
}
