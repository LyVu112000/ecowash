package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DeliveryReceiptDetailData {
    private String code;
    private String status;
    private String productTypeValue;
    private String productTypeName;
    private String customerName;
    private Instant deliveryDate;
//    private String specialInstructions;
    private String note;
    private Integer bagNumber;
    private String numberRoom;
    List<ReceivedOfDReceiptData> receivedOfDReceiptDataList;
    List<String> images;
    private String signatureStaff;
    private String signatureCustomer;

}
