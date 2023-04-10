package vuly.thesis.ecowash.core.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractProductDto extends BaseDto {

    //productItem
    private String productItem;
    private String productTypeValue;
    private String productTypeName;
    private String note;
    private boolean isCommonProduct;
    private long productItemId;
    private String pieceTypeValue;
    private String pieceTypeName;
    private boolean isOther;


}
