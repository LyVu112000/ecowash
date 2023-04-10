package vuly.thesis.ecowash.core.payload.request;


import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ProductItemUpdateRequest {

	@Size(max = 50)
	private String code;
	private String name;
	private String pieceTypeValue;
	private String productTypeValue;
	private String note;
	private long productGroupId;


}
