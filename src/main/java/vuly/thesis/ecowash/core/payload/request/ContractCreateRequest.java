package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.Instant;
import java.util.List;

@Data
public class ContractCreateRequest {
	private Boolean isExtend;
	private long contractId;
	private Instant validDate;
	private Instant expiredDate;
	private String representative;
	private String representativePosition;
	private String note;
	private String paidByValue;
	private String paymentTerm;
	private long customerId;
	private String address;
	private String phoneNumber;
	private String tax;
	@Email
	private String email;
	private List<ContractTimeCreateRequest> contractTimeCreateRequestList;
	private List<ContractProductCreateRequest> contractProductCreateRequestList;
}
