package vuly.thesis.ecowash.core.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportGeneralDto extends BaseDto{
    private long productId;
    private String productName;
    private long numberReceived;
    private long numberReceivedOfRewash;
    private long numberDeliveryActual;
    private long numberReceivedOfDebt;
    private long openNumberReceivedOfDebt;
    private String pieceTypeName;
    private String productGroupName;
    private long productGroupId;
    //HDB
    private List<ReportGeneralLaundryFormDetailDto> reportGeneralLaundryFormDetailDtoList;
}
