package vuly.thesis.ecowash.core.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.entity.Staff;
import vuly.thesis.ecowash.core.entity.Truck;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.repository.StaffRepository;
import vuly.thesis.ecowash.core.repository.TruckRepository;

import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class TruckValidation {
    private final TruckRepository truckRepository;
    private final StaffRepository staffRepository;

    public String checkExistedTruck(String truckCode) {
        Truck existedTruck = truckRepository.findFirstByCodeAndDeleted(truckCode).orElse(null);
        if (existedTruck != null) {
            return truckCode;
        }
        return null;
    }

    public void validStaff(Long staffId) {
        Optional<Staff> staffOpt = staffRepository.findById(staffId);
        if(!staffOpt.isPresent()){
            throw new AppException(4041);
        }
    }

}
