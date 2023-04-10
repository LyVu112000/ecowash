package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailDto extends BaseDto {
    private Long deliveryId;
    private long receivedId;
    private String deliveryDate;
    private String receivedDate;
    private String receivedCode;
    private String deliveryCode;
    private boolean isRewash;
    private int numberReceived;
    private int numberDelivery;
    private int debtNumber;
    private long productItemId;
    private String productItemName;
}
