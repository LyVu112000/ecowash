package vuly.thesis.ecowash.core.controller;


import lombok.RequiredArgsConstructor;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.entity.ReceivedReceipt;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;
import vuly.thesis.ecowash.core.payload.dto.ReceivedReceiptDto;
import vuly.thesis.ecowash.core.payload.request.*;
import vuly.thesis.ecowash.core.payload.response.AppListResponse;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.ReceivedReceiptService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/receivedReceipt")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReceivedReceiptController {
	@Autowired
	ReceivedReceiptService receivedReceiptService;
	@PostMapping("")
	public ResponseEntity<?> createReceipt(@Valid @RequestBody ReceivedReceiptCreateRequest request) {
		Object result = receivedReceiptService.create(request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateReceipt(@PathVariable("id") Long receiptId,
										   @Valid @RequestBody ReceivedReceiptUpdateRequest request) throws MessagingException, DocumentException, IOException {
		ReceivedReceipt result = receivedReceiptService.update(receiptId, request, CreatedSourceType.PORTAL);
		return ResponseEntity.ok(AppResponse.success(null));
	}

	@GetMapping("")
	public ResponseEntity<?> getReceiptList(ReceivedReceiptSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		request.setIsFlagError(false);
		Page<ReceivedReceiptDto> result = receivedReceiptService.findAllReceipt(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getReceipt(@PathVariable("id") Long id) {
		Object result = receivedReceiptService.getReceipt(id);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("/updateStatus/{id}")
	public ResponseEntity<?> updateStatusReceipt(@PathVariable("id") Long receiptId,
										   @Valid @RequestBody ReceiptStatusRequest request) {
		Object result = receivedReceiptService.updateStatus(receiptId, request, false);
		return ResponseEntity.ok(AppResponse.success(null));
	}

	@GetMapping("/error")
	public ResponseEntity<?> getErrorReceiptList(ReceivedReceiptSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		request.setIsFlagError(true);
		Page<ReceivedReceiptDto> result = receivedReceiptService.findAllReceipt(request, pageable);
		return ResponseEntity.ok(new AppListResponse<>(result.getContent(), result.getTotalPages(), result.getTotalElements()));
	}

	@PutMapping("/confirmError/{id}")
	public ResponseEntity<?> updateErrorReceipt(@PathVariable("id") Long receiptId,  @Valid @RequestBody FsConfirmRequest request) {
		Object result = receivedReceiptService.confirmNoError(receiptId, request);
		return ResponseEntity.ok(AppResponse.success(null));
	}

	@PutMapping("/requestReCheck/{id}")
	public ResponseEntity<?> requestReCheck(@PathVariable("id") Long receiptId) {
		Object result = receivedReceiptService.requestReCheck(receiptId);
		return ResponseEntity.ok(AppResponse.success(null));
	}
//	@PutMapping("/debtClosing/{id}")
//	@PreAuthorize("(hasResourceComponent('RECEIVED_RECEIPT') and hasActionFromResource('DEBT_CLOSING'))")
//	public ResponseEntity<?> debtClosing(@PathVariable("id") Long receiptId) {
//		Object result = receivedReceiptService.debtClosing(receiptId);
//		return ResponseEntity.ok(AppResponse.success(null));
//	}
	@GetMapping("/findByCode")
	public ResponseEntity<?> findByCode(ReceivedReceiptSearchRequest request, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		List<BriefDataDto> result = receivedReceiptService.findCodeLike(request.getCode(), request.getStatus(), pageable);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("/getDeliveryCode/{id}")
	public ResponseEntity<?> getDeliveryCode(@PathVariable("id") Long receiptId) {
		List<BriefDataDto> result = receivedReceiptService.getDeliveryCode(receiptId);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("/findAllByForDebt")
	public ResponseEntity<?> findAllByForDebt(ReceivedReceiptSearchRequest request) {
		List<BriefDataDto> result = receivedReceiptService.findAllByForDebt(request.getCustomerId(), request.getFromDate(), request.getToDate(), request.getProductTypeValue());
		return ResponseEntity.ok(AppResponse.success(result));
	}
}
