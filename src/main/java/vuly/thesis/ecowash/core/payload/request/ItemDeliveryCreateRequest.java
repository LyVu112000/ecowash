package vuly.thesis.ecowash.core.payload.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder
public class ItemDeliveryCreateRequest {
	private Long id;
	private Long productItemId;
	private Long receivedReceiptId;
	private Integer numberReceived = 0;
	private Integer numberDelivery = 0;
	private String laundryFormValue;
	private String note;
	private Integer randomNumberCheck = 0;
	private Integer numberAfterProduction = 0;
	private Integer numberDeliveryActual = 0;

}
