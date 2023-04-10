package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

@Data
public class ContractProductUpdateRequest {
    private long productItemId;
    private String note;
    private boolean isCommonProduct;
    private long id;
}
