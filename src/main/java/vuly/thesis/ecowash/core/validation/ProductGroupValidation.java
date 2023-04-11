package vuly.thesis.ecowash.core.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.entity.ProductGroup;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.request.ProductGroupCreateRequest;
import vuly.thesis.ecowash.core.repository.core.IProductGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductGroupValidation {

    private final IProductGroupRepository productGroupRepository;


    public void createCheck(ProductGroupCreateRequest request) {
        Optional<ProductGroup> optional = productGroupRepository.findByCodeOrName(request.getCode(), request.getName());
        if (optional.isPresent()) {
            ProductGroup productGroup = optional.get();
            List<Object> params = new ArrayList<>();
            if(productGroup.getCode().equalsIgnoreCase(request.getCode())) {
                params.add(productGroup.getCode());
            }
            if(productGroup.getName().equalsIgnoreCase(request.getName())) {
                params.add(productGroup.getName());
            }
            throw new AppException(HttpStatus.BAD_REQUEST, 4018, params);
        }
    }

}
