package vuly.thesis.ecowash.core.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportGeneralLaundryFormDetailDto extends BaseDto{
    private long laundryFormId;
    private String laundryFormName;
    private String laundryFormValue;
    private long numberReceived;
    private long numberReceivedOfRewash;
    private long numberDeliveryActual;
    private long numberReceivedOfDebt;
    private long openNumberReceivedOfDebt;
    private String pieceTypeName;
}
