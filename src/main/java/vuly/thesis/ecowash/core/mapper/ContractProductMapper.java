package vuly.thesis.ecowash.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.ContractProduct;
import vuly.thesis.ecowash.core.payload.dto.ContractProductDto;

@Service
@RequiredArgsConstructor
public class ContractProductMapper {

    public ContractProductDto entityToDto(ContractProduct entity) {
        if (entity == null) {
            return null;
        }
        return ContractProductDto.builder()
                .id(entity.getId())
                .createdBy(entity.getCreatedBy())
                .productItem(entity.getProductItem().getName())
                .productTypeValue(entity.getProductItem().getProductType().getValue())
                .note(entity.getNote())
                .isCommonProduct(entity.isCommonProduct())
                .productItemId(entity.getProductItem().getId())
                .pieceTypeValue(entity.getProductItem().getPieceType().getValue())
                .pieceTypeName(entity.getProductItem().getPieceType().getName())
                .productTypeName(entity.getProductItem().getProductType().getName())
                .isOther(entity.getProductItem().isOther())
                .build();
    }
}
