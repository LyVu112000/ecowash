package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

@Data
public class ContractProductCreateRequest {
    private long productItemId;
    private String note;
    private boolean isCommonProduct;
}
