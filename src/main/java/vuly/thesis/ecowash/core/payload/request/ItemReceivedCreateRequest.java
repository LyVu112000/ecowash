package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

@Data
public class ItemReceivedCreateRequest {
	private Long id;
	private Long productItemId;
	private Long deliveryReceiptId;
	private Integer numberReceived;
	private String laundryFormValue;
	private String note;
	private Integer randomNumberCheck;
	private Integer numberAfterProduction;
}
