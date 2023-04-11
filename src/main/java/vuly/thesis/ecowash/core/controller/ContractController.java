package vuly.thesis.ecowash.core.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.dto.ContractDto;
import vuly.thesis.ecowash.core.payload.request.ContractCreateRequest;
import vuly.thesis.ecowash.core.payload.request.ContractSearchRequest;
import vuly.thesis.ecowash.core.payload.request.ContractUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.ContractService;

import javax.validation.Valid;


@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContractController {
	@Autowired
	ContractService contractService;

	@PostMapping("")
	public ResponseEntity<?> createContract(@Valid @RequestBody ContractCreateRequest createRequest) {
		Object result = contractService.create(createRequest);
		return ResponseEntity.ok(AppResponse.success(result));
	}
	@PutMapping("/{id}")
	public ResponseEntity<?> updateContract(@PathVariable("id") Long id, @Valid @RequestBody ContractUpdateRequest request) {
		Object result = contractService.update(id, request);
		return ResponseEntity.ok(AppResponse.success(result));
	}
	@GetMapping("")
	public ResponseEntity<?> getContractList(ContractSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		Page<ContractDto> result = contractService.findAllContract(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}
	@GetMapping("/{id}")
	public ResponseEntity<?> getContract(@PathVariable("id") Long id) {
		ContractDto result = contractService.findContractById(id);
		return ResponseEntity.ok(AppResponse.success(result));
	}
	@PutMapping("/{id}/approvedStatus")
	public ResponseEntity<?> approvedStatus(@PathVariable("id") Long id) {
		Object result = contractService.approvedStatus(id);
		return ResponseEntity.ok(AppResponse.success(result));
	}
	@PutMapping("/{id}/cancelStatus")
	public ResponseEntity<?> cancelStatus(@PathVariable("id") Long id) {
		Object result = contractService.cancelStatus(id);
		return ResponseEntity.ok(AppResponse.success(result));
	}
	@GetMapping("/brief")
	public ResponseEntity<?> getContractBrief(Long customerId) {
		ContractDto result = contractService.getContractBriefList(customerId);
		return ResponseEntity.ok(AppResponse.success(result));
	}
}
