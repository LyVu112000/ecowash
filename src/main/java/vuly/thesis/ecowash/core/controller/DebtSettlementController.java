package vuly.thesis.ecowash.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.dto.DebtDetailDto;
import vuly.thesis.ecowash.core.payload.request.DebtSearchRequest;
import vuly.thesis.ecowash.core.payload.request.DebtSettlementRequest;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.DebtSettlementService;

import javax.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/debt")
@RequiredArgsConstructor
public class DebtSettlementController {
    @Autowired
    DebtSettlementService debtSettlementService;
    @GetMapping("/finalDebt")
    public ResponseEntity<?> getFinalDebt(DebtSearchRequest request) {
        List<DebtDetailDto> result = debtSettlementService.getFinalDebt(request);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @PostMapping("/debtSettlement")
    public ResponseEntity<?> debtSettlement(@Valid @RequestBody DebtSettlementRequest request) {
        debtSettlementService.genDeliveryByDebt(request.getDebtDetailDtos());
        return ResponseEntity.ok(AppResponse.success(null));
    }
}
