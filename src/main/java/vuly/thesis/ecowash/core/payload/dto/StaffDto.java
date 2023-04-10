package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vuly.thesis.ecowash.core.entity.type.Status;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto extends BaseDto {

    private String code;
    private String fullName;
    private String email;
    private String departmentName;
    private String phoneNumber;
    private Status status;
    private String customerName;
    private String username;
    private CustomerDto customer;
    private String note;
    private String roles;
}
