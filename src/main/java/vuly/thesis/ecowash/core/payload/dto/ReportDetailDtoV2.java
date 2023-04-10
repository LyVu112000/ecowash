package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailDtoV2 extends BaseDto {

    @Data
    @Builder(toBuilder = true)
    @JsonIgnoreType
    public static class QueryResult {
        private Long deliveryId;
        private long receivedId;
        private String deliveryDate;
        private String receivedDate;
        private String receivedCode;
        private String deliveryCode;
        private boolean isRewash;
        private int numberReceived;
        private int numberDelivery;
        private int debtNumber;
        private long productItemId;
        private String productItemName;
        private String referenceCode;
    }

    private Boolean isDelivery;
    private String date;
    private List<ReceiptDetailReport> receipts = new ArrayList<>();

    @Data
    @Builder(toBuilder = true)
    public static class ReceiptDetailReport {
        private Long id;
        private String code;
        private Integer productItemQuantity;
        private Integer debtNumber;
        private Boolean isRewash;
        private String referenceCode;
        private List<SubReceiptDetailReport> subReceipts = new ArrayList<>();
    }

    @Data
    @Builder(toBuilder = true)
    public static class SubReceiptDetailReport {
        private Long id;
        private String code;
        private String date;
        private Integer productItemQuantity;
        private Boolean isRewash;
        private String referenceCode;
        private Integer numberDelivery;
    }

}
