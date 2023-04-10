package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PlanningContractDto extends BaseDto {
    private String timeReceived;
    private String timeDelivery;
    private Integer bagNumber;
    private Integer numberOfLooseBags;
    private String note;
    private String washingTeam;

    private ContractDto contract;
    private CustomerDto customer;
    private ProductTypeDto productType;
    private String productTypeValue;
}
