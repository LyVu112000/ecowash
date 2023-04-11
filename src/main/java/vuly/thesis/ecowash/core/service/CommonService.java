package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.*;
import vuly.thesis.ecowash.core.repository.core.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class CommonService {
    @Autowired
    IProductTypeRepository productTypeRepository;
    @Autowired
    ISpecialInstructionRepository specialInstructionRepository;
    @Autowired
    ILaundryFormRepository laundryFormRepository;
    @Autowired
    IPieceTypeRepository pieceTypeRepository;
    @Autowired
    IPaidByRepository paidByRepository;


    public Object getEnum() {
        List<ProductType> productTypes = productTypeRepository.findByOrderByIdAsc();
        List<SpecialInstruction> specialInstructions = specialInstructionRepository.findByOrderByIdAsc();
        List<LaundryForm> laundryForms = laundryFormRepository.findByOrderByIdAsc();
        List<PieceType> pieceTypes = pieceTypeRepository.findByOrderByIdAsc();
        List<PaidBy> paidBys = paidByRepository.findByOrderByIdAsc();

        EnumData.EnumDataBuilder enumData = EnumData.builder()
                .laundryForms(laundryForms)
                .paidBys(paidBys)
                .productTypes(productTypes)
                .specialInstructions(specialInstructions)
                .pieceTypes(pieceTypes);
        return enumData.build();
    }

}
