package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.payload.dto.DashboardReceiptDto;
import vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryDto;
import vuly.thesis.ecowash.core.repository.DeliveryReceiptRepository;
import vuly.thesis.ecowash.core.repository.ReceivedReceiptRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.DashboardReceiptDAO;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static vuly.thesis.ecowash.core.util.DateTimeUtil.UTC_ZONE_ID;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SummaryService {
    private final DeliveryReceiptRepository deliveryReceiptRepository;
    private final ReceivedReceiptRepository receivedReceiptRepository;
    private final DashboardReceiptDAO dashboardReceiptDAO;
    private final EbstUserRequest ebstUserRequest;

    @Transactional(readOnly = true)
    public ReceiptSummaryDto getReceiptSummary() {
        ReceiptSummaryDto summary = new ReceiptSummaryDto();
        summary.setDeliveryReceiptList(deliveryReceiptRepository.getDeliveryReceiptSummary());
        summary.setReceivedReceiptList(receivedReceiptRepository.getReceivedReceiptSummary());
        summary.setRewashReceiptList(receivedReceiptRepository.getRewashReceiptSummary());
        return summary;
    }

    @Transactional(readOnly = true)
    public List<DashboardReceiptDto> getDeliveryDashboard() {
        return dashboardReceiptDAO.getDeliveryDashboard(fromDate(), toDate());
    }
    @Transactional(readOnly = true)
    public List<DashboardReceiptDto> getReceivedDashboard() {
        return dashboardReceiptDAO.getReceivedDashboard(fromDate(), toDate());
    }
    public Instant fromDate() {
        Instant fromDate = ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ebstUserRequest.currentZoneId())
                .withZoneSameInstant(ZoneId.of(UTC_ZONE_ID))
                .toInstant();
        return fromDate;
    }
    public Instant toDate() {
        Instant toDate = ZonedDateTime.of(LocalDate.now(), LocalTime.MAX, ebstUserRequest.currentZoneId())
                .truncatedTo(ChronoUnit.SECONDS)
                .withZoneSameInstant(ZoneId.of(UTC_ZONE_ID))
                .toInstant();
        return toDate;
    }


}
