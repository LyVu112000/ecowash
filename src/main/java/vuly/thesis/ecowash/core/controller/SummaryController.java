package vuly.thesis.ecowash.core.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vuly.thesis.ecowash.core.payload.dto.DashboardReceiptDto;
import vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryDto;
import vuly.thesis.ecowash.core.service.SummaryService;

import java.util.List;


@RestController
@RequestMapping("/summary")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SummaryController {

    @Autowired
    SummaryService summaryService;
    @GetMapping("/receipt")
    public ResponseEntity<?> getReceiptSummary() {
        ReceiptSummaryDto result = summaryService.getReceiptSummary();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/deliveryDashboard")
    public ResponseEntity<?> getDeliveryDashboard() {
        List<DashboardReceiptDto> result = summaryService.getDeliveryDashboard();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/receivedDashboard")
    public ResponseEntity<?> getReceivedDashboard() {
        List<DashboardReceiptDto> result = summaryService.getReceivedDashboard();
        return ResponseEntity.ok(result);
    }

}
