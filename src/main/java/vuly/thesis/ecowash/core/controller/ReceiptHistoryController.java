package vuly.thesis.ecowash.core.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vuly.thesis.ecowash.core.entity.DeliveryReceiptHistory;
import vuly.thesis.ecowash.core.entity.ReceivedReceiptHistory;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.ReceiptHistoryService;

import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReceiptHistoryController {
	@Autowired
	ReceiptHistoryService receiptHistoryService;

	@GetMapping("/delivery/{id}")
	public ResponseEntity<?> getDeliveryHistory(@PathVariable("id") Long deliveryId) {
		List<DeliveryReceiptHistory> result = receiptHistoryService.getDeliveryHistory(deliveryId);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@GetMapping("/received/{id}")
	public ResponseEntity<?> getReceivedHistory(@PathVariable("id") Long receivedId) {
		List<ReceivedReceiptHistory> result = receiptHistoryService.getReceivedHistory(receivedId);
		return ResponseEntity.ok(AppResponse.success(result));
	}
}
