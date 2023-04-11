package vuly.thesis.ecowash.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Staff;
import vuly.thesis.ecowash.core.payload.dto.StaffDto;
import vuly.thesis.ecowash.core.service.CustomerService;


@Service
@RequiredArgsConstructor
public class CustomerAccountMapper {
    private final CustomerMapper customerMapper;
    private final CustomerService customerService;

    public StaffDto entityToDto(Staff entity) {
        if (entity == null) {
            return null;
        }
        return StaffDto.builder()
                .id(entity.getId())
                .createdBy(entity.getCreatedBy())
                .code(entity.getCode())
                .customer(customerMapper.entityToDto(customerService.getCustomer(entity.getCustomerId())))
                .status(entity.getStatus())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .username(entity.getUsername())
                .note(entity.getNote())
                .roles(entity.getRole())
                .build();
    }
}
