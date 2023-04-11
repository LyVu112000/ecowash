package vuly.thesis.ecowash.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.DeliveryReceiptHistory;
import vuly.thesis.ecowash.core.entity.ReceivedReceiptHistory;
import vuly.thesis.ecowash.core.repository.DeliveryReceiptHistoryRepository;
import vuly.thesis.ecowash.core.repository.ReceivedReceiptHistoryRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReceiptHistoryService {
    private final DeliveryReceiptHistoryRepository deliveryHistoryRepository;
    private final ReceivedReceiptHistoryRepository receiptHistoryRepository;


    public List<DeliveryReceiptHistory> getDeliveryHistory(Long deliveryId) {
        return deliveryHistoryRepository.findHistoryById(deliveryId);
    }

    public List<ReceivedReceiptHistory> getReceivedHistory(Long receivedId) {
        return receiptHistoryRepository.findHistoryById(receivedId);
    }
}
