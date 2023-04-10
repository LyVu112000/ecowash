package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto extends BaseDto {

    private String code;
    private String customerName;
    private Instant validDate;
    private Instant expiredDate;
    private String address;
    private String phoneNumber;
    private String tax;
    private String email;
    private String paidBy;
    private String paymentTerm;
    private String status;
    private String representative;
    private String representativePosition;
    private String note;
    private List<ContractTimeDto> contractTimes;
    private List<ContractProductDto> contractProducts;
    private CustomerDto customer;
    private Long customerId;
    private Boolean isExtend;

}
