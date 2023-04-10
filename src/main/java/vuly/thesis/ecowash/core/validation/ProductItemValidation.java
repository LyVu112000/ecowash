package vuly.thesis.ecowash.core.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.entity.ProductItem;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.request.ProductItemCreateRequest;
import vuly.thesis.ecowash.core.payload.request.ProductItemUpdateRequest;
import vuly.thesis.ecowash.core.repository.core.IProductItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductItemValidation {

    private final IProductItemRepository productItemRepository;

    public void createCheck(ProductItemCreateRequest request) {
        Optional<ProductItem> optional = productItemRepository.findByCodeOrName(request.getCode(), request.getName());
        if (optional.isPresent()) {
            ProductItem productItem = optional.get();
            checkCodeOrName(productItem, request.getCode(), request.getName());
        }
    }
    public void updateCheck(Long id, ProductItemUpdateRequest request) {
        List<ProductItem> productItemList = productItemRepository.findByNameAndIdNot(request.getName(), id);
        for (ProductItem productItem: productItemList) {
            checkCodeOrName(productItem, request.getCode(), request.getName());
        }
    }
    public void checkCodeOrName(ProductItem productItem, String code, String name) {
        List<Object> params = new ArrayList<>();
        if(productItem.getCode().equals(code)) {
            params.add(productItem.getCode());
        }
        if(productItem.getName().equals(name)) {
            params.add(productItem.getName());
        }
        throw new AppException(HttpStatus.BAD_REQUEST, 4018, params);
    }
}
