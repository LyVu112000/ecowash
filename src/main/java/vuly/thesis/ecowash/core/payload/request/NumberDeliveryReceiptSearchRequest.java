package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

@Data
public class NumberDeliveryReceiptSearchRequest {
    private Long receiptId;
    private Long deliveryId;
}
