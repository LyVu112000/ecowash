package vuly.thesis.ecowash.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ProductType;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.repository.core.IProductTypeRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ProductTypeService {

    @Autowired
    IProductTypeRepository productTypeRepository;

    public ProductTypeService(IProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    public ProductType getByValue(String value) {
        Optional<ProductType> optional = productTypeRepository.findByValue(value);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"ProductType" + value}));
        }
    }
}
