package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeliveryReceiptUpdateRequest {
	List<Long> receivedIds;
//	private List<String> specialInstructions;
	private String numberRoom;
	private Instant deliveryDate;
	private Instant checkDate;
	private String staffCheck;
	private Integer bagNumber = 0;
	private String numberOfLooseBags;
	private Float weight;
	private String note;
	List<ItemDeliveryCreateRequest> itemDeliveryList;
	private String createdSourceType;
	private List<String> images = new ArrayList<>();
	private long truckId;
	private long driverId;
	private String receiptImage;

}
