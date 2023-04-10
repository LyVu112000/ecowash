package vuly.thesis.ecowash.core.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.repository.CustomerRepository;

@Component
@RequiredArgsConstructor
public class CustomerValidation {
    private final CustomerRepository customerRepository;

    public boolean checkExistedCustomer(String code) {
        return customerRepository.existsByCode(code);
    }

}
