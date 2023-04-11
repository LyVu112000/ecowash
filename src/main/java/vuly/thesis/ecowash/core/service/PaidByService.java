package vuly.thesis.ecowash.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.PaidBy;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.repository.core.IPaidByRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class PaidByService {

    @Autowired
    private final IPaidByRepository paidByRepository;
    public PaidByService(IPaidByRepository paidByRepository) {
        this.paidByRepository = paidByRepository;
    }


    public PaidBy getByValue(String value) {
        Optional<PaidBy> optional = paidByRepository.findByValue(value);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"PaidBy" + value}));
        }
    }
}
