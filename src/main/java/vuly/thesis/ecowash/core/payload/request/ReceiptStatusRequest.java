package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

@Data
public class ReceiptStatusRequest {
	private String status;
	private String cancelNote;
}
