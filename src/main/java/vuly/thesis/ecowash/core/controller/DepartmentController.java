package vuly.thesis.ecowash.core.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.dto.DepartmentDto;
import vuly.thesis.ecowash.core.payload.request.DepartmentCreateRequest;
import vuly.thesis.ecowash.core.payload.request.DepartmentSearchRequest;
import vuly.thesis.ecowash.core.payload.request.DepartmentUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.DepartmentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody DepartmentCreateRequest request) {
        Object result = departmentService.create(request);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody DepartmentUpdateRequest request) {
        Object result = departmentService.update(id, request);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @GetMapping("")
    public ResponseEntity<?> findAllDepartment(DepartmentSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)Pageable pageable) {
        Page<DepartmentDto> result = departmentService.findAllDepartment(request, pageable);
        return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable("id") Long id) {
        Object result = departmentService.deactive(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable("id") Long id) {
        Object result = departmentService.active(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findDepartmentById(@PathVariable("id") Long id) {
        Object result = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }
}