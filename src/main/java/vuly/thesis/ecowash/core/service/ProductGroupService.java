package vuly.thesis.ecowash.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.ProductGroup;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.ProductGroupDto;
import vuly.thesis.ecowash.core.payload.request.ProductGroupCreateRequest;
import vuly.thesis.ecowash.core.payload.request.ProductGroupSearchRequest;
import vuly.thesis.ecowash.core.payload.request.ProductGroupUpdateRequest;
import vuly.thesis.ecowash.core.repository.core.IProductGroupRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.ProductGroupDtoDAO;
import vuly.thesis.ecowash.core.validation.ProductGroupValidation;

import java.util.Optional;

@Service
public class ProductGroupService {

    @Autowired
    private ProductGroupValidation productGroupValidation;
    @Autowired
    private IProductGroupRepository productGroupRepository;
    @Autowired
    private PieceTypeService pieceTypeService;
    @Autowired
    private ProductTypeService productTypeService;
    @Autowired
    private ProductGroupDtoDAO productGroupDtoDAO;
    public ProductGroup create(ProductGroupCreateRequest request) {
        productGroupValidation.createCheck(request);
        ProductGroup productGroup = mapping(request);
        return productGroupRepository.save(productGroup);
    }

    public ProductGroup mapping(ProductGroupCreateRequest request){
        ProductGroup productGroup = new ProductGroup();
        productGroup.setCode(request.getCode());
        productGroup.setName(request.getName());
        productGroup.setNote(request.getNote());

//        if(request.getPieceTypeValue() != null){
//            productGroup.setPieceType(pieceTypeService.getByValue(request.getPieceTypeValue()));
//        }

        if(request.getProductTypeValue() != null){
            productGroup.setProductType(productTypeService.getByValue(request.getProductTypeValue()));
        }
        return productGroup;
    }

    public ProductGroup update(ProductGroupUpdateRequest request, Long id) {
//        productGroupValidation.updateCheck(id, request);
        Optional<ProductGroup> optional = productGroupRepository.findById(id);
        if (!optional.isPresent()) {
            throw new AppException(4041);
        }
        ProductGroup productGroup = optional.get();
        productGroup.setNote(request.getNote());
        productGroup.setName(request.getName());
        return productGroupRepository.save(productGroup);
    }

    @Transactional(readOnly = true)
    public Page<ProductGroupDto> findAllProductGroup(ProductGroupSearchRequest request, Pageable pageable) {
        return productGroupDtoDAO.findAll(request, pageable);
    }

    public ProductGroup deactive(Long id) {
        Optional<ProductGroup> optional = productGroupRepository.findById(id);
        if (optional.isPresent()) {
            ProductGroup productGroup = optional.get();
            productGroup.setActive(false);
            return productGroupRepository.save(productGroup);
        } else {
            throw new AppException(4041);
        }
    }

    public ProductGroup active(Long id) {
        Optional<ProductGroup> optional = productGroupRepository.findById(id);
        if (optional.isPresent()) {
            ProductGroup productGroup = optional.get();
            productGroup.setActive(true);
            return productGroupRepository.save(productGroup);
        } else {
            throw new AppException(4041);
        }
    }

    public ProductGroup getById(long id) {
        Optional<ProductGroup> optional = productGroupRepository.findById(id);
        if (optional.isPresent()) {
            ProductGroup productGroup = optional.get();
            return productGroup;
        } else {
            throw new AppException(4041);
        }
    }
}
