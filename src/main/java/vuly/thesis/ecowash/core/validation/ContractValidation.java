package vuly.thesis.ecowash.core.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.entity.Contract;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.repository.ContractRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContractValidation {
    private final ContractRepository contractRepository;
    public void checkExistedCustomer(long id) {
        Optional<Contract> contractOpt = contractRepository.findByCustomerIdAndStatus(id);
        if (contractOpt.isPresent()) {
            throw new AppException(4102);
        }
    }

}
