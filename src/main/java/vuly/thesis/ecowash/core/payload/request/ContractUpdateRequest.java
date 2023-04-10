package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;
import vuly.thesis.ecowash.core.entity.type.PaymentTerm;

import javax.validation.constraints.Email;
import java.time.Instant;
import java.util.List;

@Data
public class ContractUpdateRequest {
	private Boolean isExtend;
	private String representative;
	private String representativePosition;
	private String note;
	private String paidByValue;
	private PaymentTerm paymentTerm;
	private String address;
	private String phoneNumber;
	private String tax;
	private Instant validDate;
	private Instant expiredDate;
	@Email
	private String email;
	private List<ContractTimeUpdateRequest> contractTimeUpdateRequestList;
	private List<ContractProductUpdateRequest> contractProductUpdateRequestList;
}
