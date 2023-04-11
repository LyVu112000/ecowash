package vuly.thesis.ecowash.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.PlanningContract;
import vuly.thesis.ecowash.core.payload.dto.PlanningContractDto;

@Service
@RequiredArgsConstructor
public class PlanningContractMapper {

    private final ContractMapper contractMapper;

    public PlanningContractDto entityToDto(PlanningContract entity) {
        if (entity == null) {
            return null;
        }
        return PlanningContractDto.builder()
                .id(entity.getId())
                .timeReceived(entity.getTimeReceived())
                .timeDelivery(entity.getTimeDelivery())
                .bagNumber(entity.getBagNumber())
                .numberOfLooseBags(entity.getNumberOfLooseBags())
                .note(entity.getNote())
                .washingTeam(entity.getWashingTeam())
                .contract(contractMapper.entityToDto(entity.getContract()))
                .build();
    }
}
