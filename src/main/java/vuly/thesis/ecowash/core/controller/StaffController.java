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
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.payload.dto.StaffDto;
import vuly.thesis.ecowash.core.payload.request.StaffCreateRequest;
import vuly.thesis.ecowash.core.payload.request.StaffSearchRequest;
import vuly.thesis.ecowash.core.payload.request.StaffUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.StaffService;

import javax.validation.Valid;

@RestController
@RequestMapping("/staffs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffController {
	@Autowired
	StaffService staffService;

	@GetMapping("/{staffId}")
	public ResponseEntity<?> getStaff(@PathVariable("staffId") Long staffId) {
		Object result = staffService.getStaff(staffId);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PostMapping("")
	public ResponseEntity<?> createStaff(@Valid @RequestBody StaffCreateRequest request) {
		Object result = staffService.create(request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/{staffId}")
	@PreAuthorize("(hasAnyComponent('RESOURCE_STAFF', 'RESOURCE_ACCOUNT_CUSTOMER') and hasActionFromResource('EDIT'))")
	public ResponseEntity<?> updateStaff(@PathVariable("staffId") Long staffId,
										 @Valid @RequestBody StaffUpdateRequest request) {
		Object result = staffService.update(staffId, request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/{staffId}/updateStatus/{status}")
	public ResponseEntity<?> updateStatus(@PathVariable("staffId") Long staffId, @PathVariable("status") Status status) {
		Object result = staffService.updateStatus(staffId, status);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("")
	public ResponseEntity<?> getStaffList(StaffSearchRequest request,
										  @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		Page<StaffDto> result = staffService.getStaffList(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}

//	@PutMapping("/fs/signature/{staffId}")
//	public ResponseEntity<?> addStaffSignature(@AuthenticatedUser UserPrincipal user, @PathVariable("staffId") Long staffId,
//										 @Valid @RequestBody StaffAddSignatureRequest request) {
//		Object result = staffService.addStaffSignature(staffId, request, user);
//		return ResponseEntity.ok(AppResponse.success(result));
//	}
	@GetMapping("/customerAccount")
	public ResponseEntity<?> getCustomerAccountList(StaffSearchRequest request,
										  @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		Page<StaffDto> result = staffService.getCustomerAccountList(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}

	@GetMapping("/customerAccount/{id}")
	public ResponseEntity<?> getCustomerAccount(@PathVariable("id") Long staffId) {
		Object result = staffService.getCustomerAccount(staffId);
		return ResponseEntity.ok(AppResponse.success(result));
	}
}
