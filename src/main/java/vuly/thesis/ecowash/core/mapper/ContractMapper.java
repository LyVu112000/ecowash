package vuly.thesis.ecowash.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Contract;
import vuly.thesis.ecowash.core.payload.dto.ContractDto;

import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ContractMapper {

    private final ContractTimeMapper contractTimeMapper;
    private final ContractProductMapper contractProductMapper;
    private final CustomerMapper customerMapper;

    public ContractDto entityToDto(Contract entity) {
        if (entity == null) {
            return null;
        }
        return ContractDto.builder()
                .id(entity.getId())
                .createdBy(entity.getCreatedBy())
                .code(entity.getCode())
                .validDate(entity.getValidDate())
                .expiredDate(entity.getExpiredDate())
                .email(entity.getEmail())
                .tax(entity.getTax())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .paidBy(entity.getPaidBy().getValue())
                .paymentTerm(entity.getPaymentTerm().toString())
                .note(entity.getNote())
                .customer(customerMapper.entityToDto(entity.getCustomer()))
                .status(entity.getStatus().toString())
                .representative(entity.getRepresentative())
                .representativePosition(entity.getRepresentativePosition())
                .contractTimes(entity.getContractTimes().stream().map(contractTimeMapper::entityToDto).collect(Collectors.toList()))
                .contractProducts(entity.getContractProducts().stream().map(contractProductMapper::entityToDto).collect(Collectors.toList()))
                .isExtend(entity.isExtend())
                .build();
    }
}
