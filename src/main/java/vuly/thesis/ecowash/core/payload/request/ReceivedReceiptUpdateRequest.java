package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReceivedReceiptUpdateRequest {
	private String referenceCode;
	private Instant receivedDate;
	private Instant deliveryDate;
	private String note;
	private String numberRoom;
	private List<String> specialInstruction = new ArrayList<>();
	private String status;
	private List<String> images = new ArrayList<>();
	private Integer bagNumber;
	private Integer numberOfCheckBags;
	private String weight;
	List<ItemReceivedCreateRequest> itemReceivedCreateRequests = new ArrayList<>();
	List<Long> deliveryIds = new ArrayList<>();
	private String signatureStaff;
	private String signatureCustomer;
	private Integer deliveryBagNumber;
	private String numberOfLooseBags;
	private String createdSourceType;
	private String imagePaths; // image paths on device
	private long truckId;
	private long driverId;
	private String receiptImage;
}
