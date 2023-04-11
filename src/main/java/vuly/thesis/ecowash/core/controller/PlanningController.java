package vuly.thesis.ecowash.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.dto.PlanningDto;
import vuly.thesis.ecowash.core.payload.request.PlanningCreateRequest;
import vuly.thesis.ecowash.core.payload.request.PlanningSearchRequest;
import vuly.thesis.ecowash.core.payload.request.PlanningUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.PlanningService;

import javax.validation.Valid;

@RestController
@RequestMapping("/plannings")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping("")
    public ResponseEntity<?> findAll(PlanningSearchRequest request,
                                     @PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PlanningDto> result = planningService.findAll(request, pageable);
        return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Object result = planningService.findById(id);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody PlanningCreateRequest request) {
        Object result = planningService.create(request);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PutMapping("/{planningId}")
    public ResponseEntity<?> update(@PathVariable("planningId") Long planningId,
                                    @Valid @RequestBody PlanningUpdateRequest request) {
        Object result = planningService.update(planningId, request);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PutMapping("/updateStatus/{planningId}/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable("planningId") Long planningId,
                                          @PathVariable("status") String status) {
        Object result = planningService.updateStatus(planningId, status);
        return ResponseEntity.ok(AppResponse.success(result));
    }
}
