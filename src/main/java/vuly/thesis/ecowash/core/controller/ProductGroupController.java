package vuly.thesis.ecowash.core.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.dto.ProductGroupDto;
import vuly.thesis.ecowash.core.payload.request.ProductGroupCreateRequest;
import vuly.thesis.ecowash.core.payload.request.ProductGroupSearchRequest;
import vuly.thesis.ecowash.core.payload.request.ProductGroupUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.ProductGroupService;

import javax.validation.Valid;

@RestController
@RequestMapping("/productGroup")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductGroupController {
    private final ProductGroupService productGroupService;

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody ProductGroupCreateRequest request) {
        Object result = productGroupService.create(request);
        return ResponseEntity.ok(AppResponse.success(result));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody ProductGroupUpdateRequest request) {
        Object result = productGroupService.update(request, id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @GetMapping("")
    public ResponseEntity<?> findAllProductGroup(ProductGroupSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)Pageable pageable) {
        Page<ProductGroupDto> result = productGroupService.findAllProductGroup(request, pageable);
        return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findProductGroupById(@PathVariable Long id) {
        Object result = productGroupService.getById(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable("id") Long id) {
        Object result = productGroupService.deactive(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable("id") Long id) {
        Object result = productGroupService.active(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

}