package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReceivedOfDReceiptData {
    private String code;
    private String productTypeValue;
    private String specialInstructions;
    private String laundryForm;
    private String numberRoom;
    private List<String> images;
    private List<ItemDeliveryReceiptDto> itemDeliveryList;
}
