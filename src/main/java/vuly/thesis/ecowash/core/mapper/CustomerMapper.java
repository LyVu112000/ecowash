package vuly.thesis.ecowash.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.Customer;
import vuly.thesis.ecowash.core.payload.dto.CustomerDto;

@Service
@RequiredArgsConstructor
public class CustomerMapper {

    public CustomerDto entityToDto(Customer entity) {
        if (entity == null) {
            return null;
        }

        return CustomerDto.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .code(entity.getCode())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .active(entity.getActive().toString())
                .logo(entity.getLogo())
                .build();
    }
}
