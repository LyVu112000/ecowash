package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

@Data
public class ContractTimeCreateRequest {
    private String timeReceived;
    private String timeDelivery;
    private String note;
    private String productTypeValue;
}
