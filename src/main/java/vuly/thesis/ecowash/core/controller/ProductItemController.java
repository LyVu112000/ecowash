package vuly.thesis.ecowash.core.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.dto.ProductItemDto;
import vuly.thesis.ecowash.core.payload.request.ProductItemCreateRequest;
import vuly.thesis.ecowash.core.payload.request.ProductItemSearchRequest;
import vuly.thesis.ecowash.core.payload.request.ProductItemUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.ProductItemService;

import javax.validation.Valid;

@RestController
@RequestMapping("/productItem")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductItemController {
    private final ProductItemService productItemService;
    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody ProductItemCreateRequest request) {
        Object result = productItemService.create(request);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody ProductItemUpdateRequest request) {
        Object result = productItemService.update(id, request);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @GetMapping("")
    public ResponseEntity<?> findAllProductItem(ProductItemSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)Pageable pageable) {
        Page<ProductItemDto> result = productItemService.findAllProductItem(request, pageable);
        return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable("id") Long id) {
        Object result = productItemService.deactive(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable("id") Long id) {
        Object result = productItemService.active(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findProductItem(@PathVariable("id") Long id) {
        Object result = productItemService.getProductItemById(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @GetMapping("/specialItem")
    public ResponseEntity<?> findAllSpecialItem(ProductItemSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)Pageable pageable) {
        Page<ProductItemDto> result = productItemService.findAllSpecialItem(request, pageable);
        return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
    }

    @GetMapping("/originalItem")
    public ResponseEntity<?> findAllOriginalItem(ProductItemSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)Pageable pageable) {
        Page<ProductItemDto> result = productItemService.findAllOriginalItem(request, pageable);
        return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
    }
}