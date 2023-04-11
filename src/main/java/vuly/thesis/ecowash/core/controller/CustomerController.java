package vuly.thesis.ecowash.core.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.dto.CustomerDto;
import vuly.thesis.ecowash.core.payload.request.CustomerCreateRequest;
import vuly.thesis.ecowash.core.payload.request.CustomerSearchRequest;
import vuly.thesis.ecowash.core.payload.request.CustomerUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.CustomerService;

import javax.validation.Valid;
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	@GetMapping("/{customerId}")
	public ResponseEntity<?> getCustomers(@PathVariable("customerId") Long customerId) {
		Object result = customerService.getCustomer(customerId);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PostMapping("")
	public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerCreateRequest createRequest) {
		Object result = customerService.create(createRequest);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/{customerId}")
	public ResponseEntity<?> updateCustomer(@PathVariable("customerId") Long customerId, @Valid @RequestBody CustomerUpdateRequest request) {
		Object result = customerService.update(customerId, request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("")
	public ResponseEntity<?> getBusinessPartnerList(CustomerSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		Page<CustomerDto> result = customerService.findAllCustomer(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}

	@PostMapping("/{id}/deactivate")
	public ResponseEntity<?> deactivate(@PathVariable("id") Long id) {
		Object result = customerService.deactive(id);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PostMapping("/{id}/activate")
	public ResponseEntity<?> activate(@PathVariable("id") Long id) {
		Object result = customerService.active(id);
		return ResponseEntity.ok(AppResponse.success(result));
	}

}
