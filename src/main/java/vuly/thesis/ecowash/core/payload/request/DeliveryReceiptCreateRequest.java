package vuly.thesis.ecowash.core.payload.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Jacksonized
@Data
@Builder
public class DeliveryReceiptCreateRequest {
	private Long customerId;
	private String productTypeValue;
//	private List<String> specialInstructions;
	private String numberRoom;
	private Instant deliveryDate;
	private Instant checkDate;
	private String staffCheck;
	private Integer bagNumber;
	private String numberOfLooseBags;
	private Float weight;
	private String note;
	List<ItemDeliveryCreateRequest> itemDeliveryCreateRequests;
	List<Long> receivedIds;
	private String createdSourceType;
	private List<String> images = new ArrayList<>();
	private boolean isAutoGen = false;
	private boolean isExpress = false;
	private String signatureStaff;
	private String signatureCustomer;
	private long truckId;
	private long driverId;
	private boolean isGenByDebt;

}
