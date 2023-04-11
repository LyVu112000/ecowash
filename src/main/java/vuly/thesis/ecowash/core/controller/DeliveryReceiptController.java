package vuly.thesis.ecowash.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;
import vuly.thesis.ecowash.core.payload.dto.BriefDeliveryReceiptDto;
import vuly.thesis.ecowash.core.payload.dto.DeliveryReceiptDto;
import vuly.thesis.ecowash.core.payload.dto.NumberDeliveryReceiptDto;
import vuly.thesis.ecowash.core.payload.request.*;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.DeliveryReceiptService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/deliveryReceipt")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeliveryReceiptController {
	@Autowired
	DeliveryReceiptService deliveryReceiptService;

	@PostMapping("")
	public ResponseEntity<?> createReceipt(@Valid @RequestBody DeliveryReceiptCreateRequest request) {
		Object result = deliveryReceiptService.create(request, false, false);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateReceipt(@PathVariable("id") Long receiptId,
										   @Valid @RequestBody DeliveryReceiptUpdateRequest request) {
		Object result = deliveryReceiptService.update(receiptId, request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("")
	public ResponseEntity<?> getReceiptList(DeliveryReceiptSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		request.setIsFlagError(false);
		Page<DeliveryReceiptDto> result = deliveryReceiptService.findAllReceipt(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getReceipt(@PathVariable("id") Long id) {
		Object result = deliveryReceiptService.getReceipt(id);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/updateStatus/{id}")
	public ResponseEntity<?> updateStatusReceipt(@PathVariable("id") Long receiptId,
												 @Valid @RequestBody ReceiptStatusRequest request) {
		Object result = deliveryReceiptService.updateStatus(receiptId, request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("/error")
	public ResponseEntity<?> getErrorReceiptList(DeliveryReceiptSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		request.setIsFlagError(true);
		Page<DeliveryReceiptDto> result = deliveryReceiptService.findAllReceipt(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}

	@PutMapping("/confirmError/{id}")
	public ResponseEntity<?> updateErrorReceipt(@PathVariable("id") Long receiptId,
												@Valid @RequestBody ReceiptConfirmErrorRequest request) {
		Object result = deliveryReceiptService.confirmNoError(receiptId, request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("/numberDelivery")
	public ResponseEntity<?> getNumberDeliveryOfReceipt(NumberDeliveryReceiptSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, value = 1000) Pageable pageable) {
		Page<NumberDeliveryReceiptDto> result = deliveryReceiptService.findNumberDeliveryOfReceipt(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}

	@PutMapping("/fs/deliveryConfirm/{id}")
	public ResponseEntity<?> updateDeliveryConfirm(@PathVariable("id") Long id, @RequestBody UpdateDeliveryActualRequest updateDeliveryActualRequest) {
		Object result = deliveryReceiptService.updateDeliveryConfirm(id, updateDeliveryActualRequest);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("/brief/receiptList")
	public ResponseEntity<?> getBriefReceiptList(BriefDeliveryReceiptSearchRequest request) {
		List<BriefDeliveryReceiptDto> result = deliveryReceiptService.findAllBriefReceipt(request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("/findByCode")
	public ResponseEntity<?> findByCode(ReceivedReceiptSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		List<BriefDataDto> result = deliveryReceiptService.findCodeLike(request.getCode(), request.getStatus(), pageable);
		return ResponseEntity.ok(AppResponse.success(result));
	}

}
