package vuly.thesis.ecowash.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.SpecialInstruction;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.repository.core.ISpecialInstructionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SpecialInstructionService {

    @Autowired
    ISpecialInstructionRepository specialInstructionRepository;
    public SpecialInstructionService(ISpecialInstructionRepository specialInstructionRepository) {
        this.specialInstructionRepository = specialInstructionRepository;
    }


    public SpecialInstruction getByValue(String value) {
        Optional<SpecialInstruction> optional = specialInstructionRepository.findByValue(value);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new AppException(4041,  new ArrayList<>().add(new String[]{"SpecialInstruction" + value}));
        }
    }

    public List<String> findNameByValueIn(List<Long> ids) {
        return specialInstructionRepository.findByIdIn(ids);
    }
}
