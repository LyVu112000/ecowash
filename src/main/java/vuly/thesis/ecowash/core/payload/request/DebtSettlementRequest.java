package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;
import vuly.thesis.ecowash.core.payload.dto.DebtDetailDto;

import java.util.List;

@Data
public class DebtSettlementRequest {
	private List<DebtDetailDto> debtDetailDtos;
}
