package vuly.thesis.ecowash.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Planning;
import vuly.thesis.ecowash.core.payload.dto.PlanningDto;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanningMapper {

    private final PlanningContractMapper planningContractMapper;

    public PlanningDto entityToDto(Planning entity) {
        if (entity == null) {
            return null;
        }
        return PlanningDto.builder()
                .id(entity.getId())
                .createdBy(entity.getCreatedBy())
                .dateCreated(entity.getDateCreated())
                .modifiedBy(entity.getModifiedBy())
                .code(entity.getCode())
                .name(entity.getName())
                .fromDate(entity.getFromDate())
                .toDate(entity.getToDate())
                .status(entity.getStatus())
                .planningContracts(entity.getPlanningContracts().stream().map(planningContractMapper::entityToDto).collect(Collectors.toList()))
                .build();
    }
}
