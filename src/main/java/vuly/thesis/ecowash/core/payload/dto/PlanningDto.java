package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vuly.thesis.ecowash.core.entity.type.PlanningStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PlanningDto extends BaseDto {
    private String code;
    private String name;
    private Instant fromDate;
    private Instant toDate;
    private PlanningStatus status;
    private List<PlanningContractDto> planningContracts = new ArrayList<>();
}
