package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vuly.thesis.ecowash.core.entity.type.ReceiptStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
public class ReceiptSummaryListDto extends BaseDto {
    private ReceiptStatus status;
    private Long count;
    private String receivedCode;
    private String customerName;
    private String productType;
    private String deliveryCode;
    private Integer totalReceived;
    private Integer totalRandomCheck;
    private Integer totalAfterProduction;
    private Integer totalDelivery;

    public ReceiptSummaryListDto(ReceiptStatus status, Long count) {
        this.status = status;
        this.count = count;
    }
}
