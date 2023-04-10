package vuly.thesis.ecowash.core.payload.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerBriefDto extends BaseDto {

    private String code;
    private String fullName;

}
