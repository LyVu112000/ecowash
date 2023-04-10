package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

@Data
public class ContractTimeUpdateRequest {
    private String timeReceived;
    private String timeDelivery;
    private String note;
    private long id;
    private String productTypeValue;

}
