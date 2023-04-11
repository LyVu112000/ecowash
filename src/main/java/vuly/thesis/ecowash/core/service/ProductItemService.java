package vuly.thesis.ecowash.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.ProductItem;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.ProductItemDto;
import vuly.thesis.ecowash.core.payload.request.ProductItemCreateRequest;
import vuly.thesis.ecowash.core.payload.request.ProductItemSearchRequest;
import vuly.thesis.ecowash.core.payload.request.ProductItemUpdateRequest;
import vuly.thesis.ecowash.core.repository.ProductGroupRepository;
import vuly.thesis.ecowash.core.repository.core.IProductItemRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.ProductItemBriefDAO;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.ProductItemDtoDAO;
import vuly.thesis.ecowash.core.validation.ProductItemValidation;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ProductItemService {

    @Autowired
    private IProductItemRepository productItemRepository;
    @Autowired
    private ProductItemValidation productItemValidation;
    @Autowired
    private ProductGroupService productGroupService;
    @Autowired
    private ProductTypeService productTypeService;
    @Autowired
    private PieceTypeService pieceTypeService;
    @Autowired
    private ProductItemDtoDAO productItemDtoDAO;
    @Autowired
    private ProductItemBriefDAO productItemBriefDAO;
    @Autowired
    private ProductGroupRepository productGroupRepository;

    @Transactional(readOnly = true)
    public Page<ProductItemDto> findAllProductItem(ProductItemSearchRequest request, Pageable pageable) {
        return productItemDtoDAO.findAll(request, pageable);
    }

    public ProductItem create(ProductItemCreateRequest request) {
        if(productGroupRepository.checkExistedProductGroup(request.getProductGroupId(), false)) {
            throw new AppException(4110);
        }
        productItemValidation.createCheck( request);
        ProductItem productItem = mapping(request);
        return productItemRepository.save(productItem);
    }

    public ProductItem mapping(ProductItemCreateRequest request) {
        ProductItem productItem = new ProductItem();
        productItem.setCode(request.getCode());
        productItem.setName(request.getName());
        productItem.setNote(request.getNote());
        productItem.setOther(false);
        productItem.setProductType(productTypeService.getByValue(request.getProductTypeValue()));
        productItem.setPieceType(pieceTypeService.getByValue(request.getPieceTypeValue()));
        if (request.getProductGroupId() > 0) {
            productItem.setProductGroup(productGroupService.getById(request.getProductGroupId()));
        }
        return productItem;
    }

    public ProductItem update(Long id, ProductItemUpdateRequest request) {
        productItemValidation.updateCheck(id, request);
        if(productGroupRepository.checkExistedProductGroup(request.getProductGroupId(), false)) {
            throw new AppException(4110);
        }
        Optional<ProductItem> optional = productItemRepository.findById(id);
        if (!optional.isPresent()) {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"ProductItem" + id}));
        }
        ProductItem productItem = optional.get();
        productItem.setName(request.getName());
        productItem.setNote(request.getNote());
        productItem.setProductType(productTypeService.getByValue(request.getProductTypeValue()));
        productItem.setPieceType(pieceTypeService.getByValue(request.getPieceTypeValue()));
        if (request.getProductGroupId() > 0) {
            productItem.setProductGroup(productGroupService.getById(request.getProductGroupId()));
        }
        return productItemRepository.save(productItem);
    }

    public ProductItem deactive(Long id) {
//        productItemValidation.checkBeforeDeactive(id);
        Optional<ProductItem> optional = productItemRepository.findById(id);
        if (optional.isPresent()) {
            ProductItem productItem = optional.get();
            productItem.setActive(false);
            return productItemRepository.save(productItem);
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"ProductItem" + id}));
        }
    }
    public ProductItem active(Long id) {
        Optional<ProductItem> optional = productItemRepository.findById(id);
        if (optional.isPresent()) {
            ProductItem productItem = optional.get();
            productItem.setActive(true);
            return productItemRepository.save(productItem);
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"ProductItem" + id}));
        }
    }


    public ProductItem getProductItemById(Long id) {
        Optional<ProductItem> optional = productItemRepository.findById(id);
        if (optional.isPresent()) {
            ProductItem productItem = optional.get();
            return productItem;
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"ProductItem" + id}));
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductItemDto> findAllSpecialItem(ProductItemSearchRequest request, Pageable pageable) {
        request.setProductTypeValue("special_product");
        return productItemBriefDAO.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductItemDto> findAllOriginalItem(ProductItemSearchRequest request, Pageable pageable) {
        request.setProductTypeValue("ordinary_product");
        return productItemBriefDAO.findAll(request, pageable);
    }
}
