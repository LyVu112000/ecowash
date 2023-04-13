package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.document.generator.ReportGenerator;
import vuly.thesis.ecowash.core.entity.Customer;
import vuly.thesis.ecowash.core.entity.ProductItem;
import vuly.thesis.ecowash.core.payload.dto.ReportDetailDtoV2;
import vuly.thesis.ecowash.core.payload.dto.ReportDetailGeneralDto;
import vuly.thesis.ecowash.core.payload.dto.ReportGeneralDto;
import vuly.thesis.ecowash.core.payload.dto.ReportGeneralLaundryFormDetailDto;
import vuly.thesis.ecowash.core.payload.request.ReportGeneralSearchRequest;
import vuly.thesis.ecowash.core.util.DateTimeUtil;
import vuly.thesis.ecowash.core.util.EbstUserRequest;
import vuly.thesis.ecowash.core.util.StringUtil;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceV2 {
    @Autowired
    EntityManager entityManager;
    @Autowired
    EbstUserRequest ebstUserRequest;
    @Autowired
    ProductTypeService productTypeService;

    private final CustomerService customerService;
    private final ReportGenerator reportGenerator;
    private final SpecialInstructionService specialInstructionService;
    private final ProductItemService productItemService;
    private final LaundryFormService laundryFormService;

    public List<ReportGeneralDto> getReportGeneral(ReportGeneralSearchRequest request) {
        List<String> special = new ArrayList<>();
        if (request.getSpecialInstructions() != null && !request.getSpecialInstructions().isEmpty()) {
            special = new ArrayList<String>(Arrays.asList(request.getSpecialInstructions().split(",")));
        }

        int yearInit = request.getFromDate().atZone(ebstUserRequest.currentZoneId()).getYear();
        LocalDateTime initDate = LocalDateTime.of(yearInit, 1, 1, 0, 0);
        Instant instant = initDate.atZone(ebstUserRequest.currentZoneId()).toInstant();
        log.info(instant.toString());
        log.info(DateTimeUtil.convertToUTC(instant, ebstUserRequest) + "");

//        request.getProductType().equalsIgnoreCase("ordinary_product")
        String storedProcedureString = request.getProductType().equalsIgnoreCase("ordinary_product") ? "sp_GetReportOfOrdinaryProduct_noRW"
                : request.getProductType().equalsIgnoreCase("special_product") ? "sp_GetDebtReportOfSpecialProduct_noRW" : "sp_GetReportOfProduct_RW";

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery(storedProcedureString)
                .registerStoredProcedureParameter("customerId", Long.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("from_date", Instant.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("to_date", Instant.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("count_instruction", Integer.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("special_instructions", String.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("init_date", Instant.class,
                        ParameterMode.IN)
                .setParameter("customerId", request.getCustomerId())
                .setParameter("from_date", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest))
                .setParameter("to_date", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest))
                .setParameter("count_instruction", special.size())
                .setParameter("special_instructions", request.getSpecialInstructions() != null
                        && !request.getSpecialInstructions().isEmpty() ? request.getSpecialInstructions() : "")
                .setParameter("init_date", instant);
        if (request.getProductType().equalsIgnoreCase("ordinary_product") || request.getProductType().equalsIgnoreCase("special_product")) {
            query.registerStoredProcedureParameter("productTypeId", Long.class,
                            ParameterMode.IN)
                    .setParameter("productTypeId", productTypeService.getByValue(request.getProductType()).getId());
        }
        query.execute();
        List<Object[]> data = query.getResultList();
        List<ReportGeneralDto> reportGeneralDtoList = new ArrayList<>();
        long tProductId = 0;
        for (Object[] object : data) {
            if (!request.getProductType().equalsIgnoreCase("special_product")) {
                ReportGeneralDto reportGeneralDto = ReportGeneralDto.builder()
                        .productId(Long.parseLong(object[0].toString()))
                        .productName(object[1].toString())
                        .numberReceived(Long.parseLong(object[2].toString()))
//                            .numberReceivedOfRewash(Long.parseLong(object[3].toString()))
                        .numberDeliveryActual(Long.parseLong(object[3].toString()))
                        .numberReceivedOfDebt(Long.parseLong(object[4].toString()))
                        .openNumberReceivedOfDebt(Long.parseLong(object[5].toString()))
                        .pieceTypeName(object[6].toString())
                        .productGroupName(object[7].toString())
                        .productGroupId(Long.parseLong(object[8].toString()))
                        .build();
                reportGeneralDtoList.add(reportGeneralDto);
            } else {
                long productId = Long.parseLong(object[0].toString());
                if (tProductId != productId) {
                    tProductId = productId;
                    ReportGeneralDto reportGeneralDto = ReportGeneralDto.builder()
                            .productId(Long.parseLong(object[0].toString()))
                            .productName(object[1].toString())
                            .pieceTypeName(object[9].toString())
                            .productGroupName(object[10].toString())
                            .productGroupId(Long.parseLong(object[11].toString()))
                            .build();
                    List<ReportGeneralLaundryFormDetailDto> reportGeneralLaundryFormDetailDtoList = new ArrayList<>();
                    for (Object[] object1 : data) {
                        if (productId == Long.parseLong(object1[0].toString())) {
                            ReportGeneralLaundryFormDetailDto it = ReportGeneralLaundryFormDetailDto.builder()
                                    .laundryFormId(Long.parseLong(object1[2].toString()))
                                    .laundryFormName(object1[3].toString())
                                    .numberReceived(Long.parseLong(object1[4].toString()))
//                                            .numberReceivedOfRewash(Long.parseLong(object1[5].toString()))
                                    .numberDeliveryActual(Long.parseLong(object1[5].toString()))
                                    .numberReceivedOfDebt(Long.parseLong(object1[6].toString()))
                                    .openNumberReceivedOfDebt(Long.parseLong(object1[7].toString()))
                                    .laundryFormValue(object1[8].toString())
                                    .build();
                            reportGeneralLaundryFormDetailDtoList.add(it);
                        }
                    }
                    reportGeneralDto.setReportGeneralLaundryFormDetailDtoList(reportGeneralLaundryFormDetailDtoList);
                    reportGeneralDtoList.add(reportGeneralDto);
                }
            }
        }
        return reportGeneralDtoList;
    }

    public ReportDetailGeneralDto getReceiptDetailReportV2(ReportGeneralSearchRequest request) {
        List<String> special = new ArrayList<>();
        if (request.getSpecialInstructions() != null && !request.getSpecialInstructions().isEmpty()) {
            special = new ArrayList<>(Arrays.asList(request.getSpecialInstructions().split(",")));
        }
        StoredProcedureQuery queryDetail = entityManager
                .createStoredProcedureQuery(request.getIsDelivery() ? "sp_GetReportDeliveryDetail_ByRW" : "sp_GetReportReceivedDetail")
                .registerStoredProcedureParameter("customerId", Long.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("productItemId", Long.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("from_date", Instant.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("to_date", Instant.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("count_instruction", Integer.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("special_instructions", String.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("laundry_form_value", String.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("product_type_id", Long.class,
                        ParameterMode.IN)
                .registerStoredProcedureParameter("is_rewash", Boolean.class,
                        ParameterMode.IN)
                .setParameter("customerId", request.getCustomerId())
                .setParameter("productItemId", request.getProductItemId())
                .setParameter("from_date", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest))
                .setParameter("to_date", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest))
                .setParameter("count_instruction", special.size())
                .setParameter("special_instructions", request.getSpecialInstructions() != null && !request.getSpecialInstructions().isEmpty() ? request.getSpecialInstructions() : "")
                .setParameter("laundry_form_value", request.getLaundryFormValue() != null ? request.getLaundryFormValue() : "")
                .setParameter("product_type_id", request.getProductType().equalsIgnoreCase("ordinary_product")
                        || request.getProductType().equalsIgnoreCase("special_product")
                        ? productTypeService.getByValue(request.getProductType()).getId() : 0)
                .setParameter("is_rewash", request.getIsRewash());

        queryDetail.execute();
        List<Object[]> resultList = queryDetail.getResultList();
        List<ReportDetailDtoV2.QueryResult> reportDetailDataQueryList = new ArrayList<>();
        long sumNumberReceived = 0;
        long sumNumberDelivery = 0;
        long sumNumberDebt = 0;
        List<String> receivedCode = new ArrayList<>();
        for (Object[] object : resultList) {
            ReportDetailDtoV2.QueryResult reportDetailDataQuery = ReportDetailDtoV2.QueryResult.builder()
                    .deliveryId(object[0] != null ? Long.parseLong(object[0].toString()) : null)
                    .receivedId(Long.parseLong(object[1].toString()))
                    .deliveryDate(object[2] != null ? DateTimeUtil.revertFromUTC(object[2].toString(), ebstUserRequest.currentZoneId()) : null)
                    .deliveryCode(object[3] != null ? object[3].toString() : null)
                    .receivedDate(DateTimeUtil.revertFromUTC(object[4].toString(), ebstUserRequest.currentZoneId()))
                    .receivedCode(object[5].toString())
                    .isRewash(Boolean.parseBoolean(object[6].toString()))
                    .numberReceived(Integer.parseInt(object[7].toString()))
                    .numberDelivery(object[8] != null ? Integer.parseInt(object[8].toString()) : null)
                    .debtNumber(Integer.parseInt(object[9].toString()))
                    .referenceCode(object[10].toString())
                    .build();
            sumNumberDelivery += Integer.parseInt(object[8].toString());
            if (!receivedCode.contains(object[5].toString())) {
                sumNumberReceived += Integer.parseInt(object[7].toString());
                sumNumberDebt += Integer.parseInt(object[9].toString());
            }
            receivedCode.add(object[5].toString());
            reportDetailDataQueryList.add(reportDetailDataQuery);
        }

        List<ReportDetailDtoV2> reportDetailDtoV2List = new ArrayList<>();
        Map<String, List<ReportDetailDtoV2.QueryResult>> rpDetailGroupByDate = new HashMap<>();

        if (request.getIsDelivery()) {
            rpDetailGroupByDate = reportDetailDataQueryList.stream()
                    .collect(Collectors.groupingBy(e -> e.getDeliveryDate().substring(0, 10)));
        } else {
            rpDetailGroupByDate = reportDetailDataQueryList.stream()
                    .collect(Collectors.groupingBy(e -> e.getReceivedDate().substring(0, 10)));
        }

        for (String date : rpDetailGroupByDate.keySet().stream().sorted().toList()) {
            ReportDetailDtoV2 reportDetailDtoV2 = ReportDetailDtoV2.builder()
                    .isDelivery(request.getIsDelivery())
                    .date(DateTimeUtil.formatToString(LocalDate.parse(date)))
                    .build();

            List<ReportDetailDtoV2.ReceiptDetailReport> receipts = new ArrayList<>();
            for (ReportDetailDtoV2.QueryResult rpDetailByDate : rpDetailGroupByDate.get(date)) {
                ReportDetailDtoV2.ReceiptDetailReport receipt;
                if (request.getIsDelivery()) {
                    if (receipts.stream().filter(e -> e.getCode().equals(rpDetailByDate.getDeliveryCode())).findFirst().isPresent())
                        continue;
                    receipt = ReportDetailDtoV2.ReceiptDetailReport.builder()
                            .id(rpDetailByDate.getDeliveryId())
                            .code(rpDetailByDate.getDeliveryCode())
                            .productItemQuantity(rpDetailByDate.getNumberDelivery())
                            .debtNumber(rpDetailByDate.getDebtNumber())
                            .build();
                } else {
                    if (receipts.stream().filter(e -> e.getCode().equals(rpDetailByDate.getReceivedCode())).findFirst().isPresent())
                        continue;
                    receipt = ReportDetailDtoV2.ReceiptDetailReport.builder()
                            .id(rpDetailByDate.getReceivedId())
                            .code(rpDetailByDate.getReceivedCode())
                            .referenceCode(rpDetailByDate.getReferenceCode())
                            .productItemQuantity(rpDetailByDate.getNumberReceived())
                            .debtNumber(rpDetailByDate.getDebtNumber())
                            .isRewash(rpDetailByDate.isRewash())
                            .build();
                }

                List<ReportDetailDtoV2.SubReceiptDetailReport> subReceipts = new ArrayList<>();
                List<ReportDetailDtoV2.QueryResult> rpDetailByCodeList = new ArrayList<>();
                if (request.getIsDelivery()) {
                    rpDetailByCodeList = rpDetailGroupByDate.get(date)
                            .stream()
                            .filter(e -> e.getDeliveryCode().equals(rpDetailByDate.getDeliveryCode()))
                            .collect(Collectors.toList());
                } else {
                    if (rpDetailByDate.getDeliveryDate() != null) {
                        rpDetailByCodeList = rpDetailGroupByDate.get(date)
                                .stream()
                                .filter(e -> e.getReceivedCode().equals(rpDetailByDate.getReceivedCode()))
                                .collect(Collectors.toList());
                    }
                }

                for (ReportDetailDtoV2.QueryResult rpDetailByCode : rpDetailByCodeList) {
                    ReportDetailDtoV2.SubReceiptDetailReport subReceipt;
                    if (request.getIsDelivery()) {
                        subReceipt = ReportDetailDtoV2.SubReceiptDetailReport.builder()
                                .id(rpDetailByCode.getReceivedId())
                                .code(rpDetailByCode.getReceivedCode())
                                .referenceCode(rpDetailByCode.getReferenceCode())
                                .date(DateTimeUtil.formatToString(LocalDate.parse(rpDetailByCode.getReceivedDate().substring(0, 10))))
                                .productItemQuantity(rpDetailByCode.getNumberReceived())
                                .isRewash(rpDetailByCode.isRewash())
                                .numberDelivery(rpDetailByCode.getNumberDelivery())
                                .build();
                    } else {
                        subReceipt = ReportDetailDtoV2.SubReceiptDetailReport.builder()
                                .id(rpDetailByCode.getDeliveryId())
                                .code(rpDetailByCode.getDeliveryCode())
                                .date(DateTimeUtil.formatToString(LocalDate.parse(rpDetailByCode.getDeliveryDate().substring(0, 10))))
                                .productItemQuantity(rpDetailByCode.getNumberDelivery())
                                .build();
                    }
                    subReceipts.add(subReceipt);
                }
                receipt.setSubReceipts(subReceipts);
                receipts.add(receipt);
            }
            reportDetailDtoV2.setReceipts(receipts);
            reportDetailDtoV2List.add(reportDetailDtoV2);
        }

        ReportDetailGeneralDto reportDetailGeneralDto = ReportDetailGeneralDto.builder()
                .data(reportDetailDtoV2List)
                .sumNumberDelivery(sumNumberDelivery)
                .sumNumberReceived(sumNumberReceived)
                .sumNumberDebt(sumNumberDebt).build();

        return reportDetailGeneralDto;
    }

    public File generateGeneralReport(ReportGeneralSearchRequest request) throws IOException {
        Customer customer = customerService.findById(request.getCustomerId());
        List<ReportGeneralDto> reportGeneralDtoList = this.getReportGeneral(request);

        String specialInstructions = "";
        if (!StringUtil.isEmpty(request.getSpecialInstructions())) {
            List<Long> specialInstructionIds = Arrays.stream(request.getSpecialInstructions().split(",")).map(Long::valueOf).collect(Collectors.toList());
            specialInstructions = specialInstructionService.findNameByValueIn(specialInstructionIds).stream().collect(Collectors.joining(", "));
        }

        return reportGenerator.generateGeneralReportXlsxFile(customer.getFullName(), request.getProductType(),
                specialInstructions, request.getFromDate(), request.getToDate(), reportGeneralDtoList);
    }

    public File generateDetailReport(ReportGeneralSearchRequest request) throws IOException {
        Customer customer = customerService.findById(request.getCustomerId());
        ProductItem productItem = productItemService.getProductItemById(request.getProductItemId());

        String laundryFormName = "";
        if (!StringUtil.isEmpty(request.getLaundryFormValue())) {
            laundryFormName = laundryFormService.getByValue(request.getLaundryFormValue()).getName();
        }

        String specialInstructions = "";
        if (!StringUtil.isEmpty(request.getSpecialInstructions())) {
            List<Long> specialInstructionIds = Arrays.stream(request.getSpecialInstructions().split(",")).map(Long::valueOf).collect(Collectors.toList());
            specialInstructions = specialInstructionService.findNameByValueIn(specialInstructionIds).stream().collect(Collectors.joining(", "));
        }

        List<ReportDetailDtoV2> reportDetailDtoV2List = this.getReceiptDetailReportV2(request).getData();
        return reportGenerator.generateDetailReportXlsxFile(request.getIsDelivery(), customer.getFullName(), productItem.getName(),
                productItem.getPieceType().getName(), laundryFormName, specialInstructions, request.getFromDate(), request.getToDate(), reportDetailDtoV2List);
    }
}
