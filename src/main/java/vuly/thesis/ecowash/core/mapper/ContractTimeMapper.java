package vuly.thesis.ecowash.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ContractTime;
import vuly.thesis.ecowash.core.payload.dto.ContractTimeDto;


@Service
@RequiredArgsConstructor
public class ContractTimeMapper {

    public ContractTimeDto entityToDto(ContractTime entity) {
        if (entity == null) {
            return null;
        }
        return ContractTimeDto.builder()
                .id(entity.getId())
                .createdBy(entity.getCreatedBy())
                .timeReceived(entity.getTimeReceived())
                .timeDelivery(entity.getTimeDelivery())
                .note(entity.getNote())
                .productTypeValue(entity.getProductType().getValue())
                .build();
    }
}
