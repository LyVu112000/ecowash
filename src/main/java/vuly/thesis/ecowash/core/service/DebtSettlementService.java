package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.DeliveryReceipt;
import vuly.thesis.ecowash.core.entity.ReceivedReceipt;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.DebtDetailDto;
import vuly.thesis.ecowash.core.payload.request.DebtSearchRequest;
import vuly.thesis.ecowash.core.payload.request.DeliveryReceiptCreateRequest;
import vuly.thesis.ecowash.core.payload.request.ItemDeliveryCreateRequest;
import vuly.thesis.ecowash.core.util.DateTimeUtil;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class DebtSettlementService {
    @Autowired
    EntityManager entityManager;
    @Autowired
    EbstUserRequest ebstUserRequest;
    @Autowired
    DeliveryReceiptService deliveryReceiptService;
    @Autowired
    ReceivedReceiptService receivedReceiptService;
    @Autowired
    ProductTypeService productTypeService;

    public List<DebtDetailDto> getFinalDebt(DebtSearchRequest request) {
        StoredProcedureQuery queryDetailFake = entityManager
                .createStoredProcedureQuery("sp_GetDataDebt")
                .registerStoredProcedureParameter("customerId", Long.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("from_date", Instant.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("to_date", Instant.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("product_type_id", Long.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("product_item_id", String.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("received_receipt_id", String.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("tenantId", Long.class,
                        ParameterMode.IN)
                .setParameter("customerId", request.getCustomerId())
                .setParameter("from_date", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest))
                .setParameter("to_date", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest))
                .setParameter("product_type_id", productTypeService.getByValue(request.getProductType()).getId())
                .setParameter("product_item_id", request.getProductItemIds() != null ? request.getProductItemIds() : "")
                .setParameter("received_receipt_id", request.getReceivedReceiptIds() != null ? request.getReceivedReceiptIds() : "");
        queryDetailFake.execute();
        List<Object[]> data = queryDetailFake.getResultList();
        List<DebtDetailDto> finalDebtDtoList = new ArrayList<>();
        for (Object[] object : data) {
            DebtDetailDto reportDetailDto = DebtDetailDto.builder()
                    .productItemId(Long.parseLong(object[0].toString()))
                    .productItemName(object[1].toString())
                    .receivedReceiptId(Long.parseLong(object[2].toString()))
                    .receivedCode(object[3].toString())
                    .numberReceived(Integer.parseInt(object[4].toString()))
                    .numberDelivery(object[5] != null ? Integer.parseInt(object[5].toString()) : null)
                    .debtNumber(Integer.parseInt(object[6].toString()))
                    .numberAfterProduction(Integer.parseInt(object[7].toString()))
                    .laundryFormValue(object[8] != null ? object[8].toString() : null)
                    .randomNumberCheck(Integer.parseInt(object[9].toString()))
                    .pieceTypeName(object[10].toString())
                    .referenceCode(object[11].toString())
                    .receivedDate(object[12] != null ?
                            DateTimeUtil.revertFromUTCNoS(object[12].toString(), ebstUserRequest.currentZoneId()) : null)
                    .build();
            finalDebtDtoList.add(reportDetailDto);
        }
        return finalDebtDtoList;
    }

    public DeliveryReceipt genDeliveryByDebt(List<DebtDetailDto> reportDetailDtos) {
        if(reportDetailDtos.size() > 0){
            ReceivedReceipt receivedReceipt = receivedReceiptService.getReceipt(reportDetailDtos.get(0).getReceivedReceiptId());
            List<ItemDeliveryCreateRequest> itemDeliveryCreateRequests = new ArrayList<>();
            List<Long> receivedIds = new ArrayList<>();
            for (DebtDetailDto item : reportDetailDtos) {
//                ReceivedReceipt tR = receivedReceiptService.getReceipt(reportDetailDtos.get(0).getReceivedId());
//                if(!tR.getStatus().equals(ReceiptStatus.PACKING)){
//                    throw new AppException(4303);
//                }
                ItemDeliveryCreateRequest request = ItemDeliveryCreateRequest.builder()
                        .productItemId(item.getProductItemId())
                        .receivedReceiptId(item.getReceivedReceiptId())
                        .numberReceived(item.getNumberReceived())
                        .numberDelivery(item.getDebtNumber())
                        .numberAfterProduction(item.getNumberAfterProduction())
                        .laundryFormValue(item.getLaundryFormValue())
                        .note("")
                        .randomNumberCheck(item.getRandomNumberCheck()).build();
                itemDeliveryCreateRequests.add(request);
                receivedIds.add(item.getReceivedReceiptId());
            }

            DeliveryReceiptCreateRequest request = DeliveryReceiptCreateRequest.builder()
                    .customerId(receivedReceipt.getCustomer().getId())
                    .productTypeValue(receivedReceipt.getProductType().getValue())
                    .bagNumber(0)
                    .numberOfLooseBags("")
                    .weight(0f)
                    .note("")
                    .receivedIds(receivedIds)
                    .itemDeliveryCreateRequests(itemDeliveryCreateRequests)
                    .isExpress(false)
                    .isAutoGen(false)
                    .isGenByDebt(true)
                    .build();

            return deliveryReceiptService.create(request, false, true);
        }
        throw new AppException(4307);
    }

}
