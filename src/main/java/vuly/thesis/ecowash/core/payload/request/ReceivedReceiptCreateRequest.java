package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReceivedReceiptCreateRequest {
	private String referenceCode;
	private String productTypeValue;
	private long contractId;
	private long customerId;
	private Instant receivedDate;
	private Instant deliveryDate;
	private Boolean isRewash;
	private String note;
	private String numberRoom;
	private List<String> specialInstruction = new ArrayList<>();
	private String status;
	private List<String> images = new ArrayList<>();;
	private Integer bagNumber;
	private String weight;
	List<ItemReceivedCreateRequest> itemReceivedCreateRequests = new ArrayList<>();
	List<Long> deliveryIds = new ArrayList<>();
	private String imagePaths; // image paths on device
	private String signatureStaff;
	private String signatureCustomer;
	private boolean isAutoGen = false;
	private long truckId;
	private long driverId;

	private String createdSourceType;
	private String numberOfLooseBags;
	private String receiptImage;

}
