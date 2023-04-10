package vuly.thesis.ecowash.core.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.entity.Customer;
import vuly.thesis.ecowash.core.entity.Department;
import vuly.thesis.ecowash.core.entity.Staff;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.repository.CustomerRepository;
import vuly.thesis.ecowash.core.repository.StaffRepository;
import vuly.thesis.ecowash.core.repository.core.IDepartmentRepository;

import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class StaffValidation {
    private final StaffRepository staffRepository;
    private final IDepartmentRepository departmentRepository;
    private final CustomerRepository customerRepository;

    public String checkExistedStaff(String email, String staffCode, String username) {
        Staff existedStaff = staffRepository.findFirstByEmailOrCodeOrUsernameAndDeleted(email, staffCode, username).orElse(null);
        if (existedStaff != null) {
            if (email.equals(existedStaff.getEmail()) || email.toLowerCase().equals(existedStaff.getEmail().toLowerCase())) {
                return email;
            } else if (staffCode.equals(existedStaff.getCode()) || staffCode.toLowerCase().equals(existedStaff.getCode().toLowerCase())) {
                return staffCode;
            } else {
                return username;
            }
        }
        return null;
    }

    public void validDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(()-> new AppException(4041));
        if(!department.isActive()){
            throw new AppException(4204);
        }
    }

    public void validCustomer(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.getById(customerId);
        if(!customerOpt.isPresent()){
            throw new AppException(4041);
        }
    }

}
