package vuly.thesis.ecowash.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.payload.dto.TruckDto;
import vuly.thesis.ecowash.core.payload.request.TruckCreateRequest;
import vuly.thesis.ecowash.core.payload.request.TruckSearchRequest;
import vuly.thesis.ecowash.core.payload.request.TruckUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.TruckService;

import javax.validation.Valid;

@RestController
@RequestMapping("/trucks")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TruckController {
	@Autowired
	TruckService truckService;

	@GetMapping("/{truckId}")
	public ResponseEntity<?> getTruck(@PathVariable("truckId") Long truckId) {
		Object result = truckService.getTruck(truckId);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PostMapping("")
	public ResponseEntity<?> createTruck(@Valid @RequestBody TruckCreateRequest request) {
		Object result = truckService.create(request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/{truckId}")
	public ResponseEntity<?> updateTruck(@PathVariable("truckId") Long truckId,
										 @Valid @RequestBody TruckUpdateRequest request) {
		Object result = truckService.update(truckId, request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/{truckId}/updateStatus/{status}")
	public ResponseEntity<?> updateStatus(@PathVariable("truckId") Long truckId, @PathVariable("status") Status status) {
		Object result = truckService.updateStatus(truckId, status);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("")
	public ResponseEntity<?> getTruckList(TruckSearchRequest request,
										  @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		Page<TruckDto> result = truckService.getTruckList(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}
}
