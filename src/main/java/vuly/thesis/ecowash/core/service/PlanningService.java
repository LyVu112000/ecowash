package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.*;
import vuly.thesis.ecowash.core.entity.type.ContractStatus;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;
import vuly.thesis.ecowash.core.entity.type.PlanningStatus;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.PlanningDto;
import vuly.thesis.ecowash.core.payload.request.*;
import vuly.thesis.ecowash.core.repository.ContractRepository;
import vuly.thesis.ecowash.core.repository.CustomerRepository;
import vuly.thesis.ecowash.core.repository.PlanningRepository;
import vuly.thesis.ecowash.core.repository.ProductTypeRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.PlanningContractDAO;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.PlanningDAO;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static vuly.thesis.ecowash.core.util.DateTimeUtil.UTC_ZONE_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;
    private final ProductTypeRepository productTypeRepository;
    private final EbstUserRequest ebstUserRequest;
    private final ReceivedReceiptService receivedReceiptService;
    private final PlanningDAO planningDAO;
    private final PlanningContractDAO planningContractDAO;

    public Planning create(PlanningCreateRequest request) {
        Integer sequenceNumber = planningRepository.findMaxSequenceNumber().orElse(0) + 1;
        Planning planning = new Planning().toBuilder()
                .code(String.format("%s-%03d", "PL", sequenceNumber))
                .name(request.getName())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .status(PlanningStatus.PENDING)
                .sequenceNumber(sequenceNumber)
                .build();

        if (!request.getPlanningContracts().isEmpty()) {
            List<Long> contractIdsRequest = request.getPlanningContracts().stream().map(plcRequest -> plcRequest.getContractId()).collect(Collectors.toList());
            Map<Long, Contract> contractsGroupById = contractRepository.findByIdInAndStatus(contractIdsRequest, ContractStatus.APPROVED)
                    .stream().collect(Collectors.toMap(c -> c.getId(), c -> c));
            List<Long> customerIdsRequest = request.getPlanningContracts().stream().map(plcRequest -> plcRequest.getCustomerId()).collect(Collectors.toList());
            Map<Long, Customer> customersGroupById = customerRepository.findByIdIn(customerIdsRequest)
                    .stream().collect(Collectors.toMap(c -> c.getId(), c -> c));

            List<PlanningContract> planningContracts = new ArrayList<>();
            for (PlanningContractCreateRequest plcCreateRequest : request.getPlanningContracts()) {
                planningContracts.add(new PlanningContract().toBuilder()
                        .planning(planning)
                        .contract(contractsGroupById.get(plcCreateRequest.getContractId()))
                        .customer(customersGroupById.get(plcCreateRequest.getCustomerId()))
                        .timeReceived(plcCreateRequest.getTimeReceived())
                        .timeDelivery(plcCreateRequest.getTimeDelivery())
                        .bagNumber(plcCreateRequest.getBagNumber())
                        .numberOfLooseBags(plcCreateRequest.getNumberOfLooseBags())
                        .note(plcCreateRequest.getNote())
                        .washingTeam(plcCreateRequest.getWashingTeam())
                        .productType(productTypeRepository.findByValue(plcCreateRequest.getProductTypeValue()).orElseThrow(() -> new AppException(4041)))
                        .build());
            }
            planning.setPlanningContracts(planningContracts);
        }
        return planningRepository.save(planning);
    }

    public Planning update(Long planningId, PlanningUpdateRequest request) {
        Planning planning = planningRepository.findById(planningId).orElseThrow(() -> new AppException(4041));
        if (planning.getStatus() != PlanningStatus.PENDING) {
            throw new AppException(4101);
        }

        planning.setName(request.getName());
        planning.setFromDate(request.getFromDate());
        planning.setToDate(request.getToDate());
//        planning.setStatus(request.getStatus());

        List<PlanningContract> newPlanningContracts = new ArrayList<>();
        if (!request.getPlanningContracts().isEmpty()) {
            Map<Long, PlanningContract> planningContractsGroupById = planning.getPlanningContracts()
                    .stream().collect(Collectors.toMap(plc -> plc.getId(), plc -> plc));
            List<Long> contractIdsRequest = request.getPlanningContracts().stream().map(plcRequest -> plcRequest.getContractId()).collect(Collectors.toList());
            Map<Long, Contract> contractsGroupById = contractRepository.findByIdInAndStatus(contractIdsRequest, ContractStatus.APPROVED)
                    .stream().collect(Collectors.toMap(c -> c.getId(), c -> c));
            List<Long> customerIdsRequest = request.getPlanningContracts().stream().map(plcRequest -> plcRequest.getCustomerId()).collect(Collectors.toList());
            Map<Long, Customer> customersGroupById = customerRepository.findByIdIn(customerIdsRequest)
                    .stream().collect(Collectors.toMap(c -> c.getId(), c -> c));
//            if (contractsGroupById.size() == 0) {
//                throw new AppException(4108);
//            }
            for (PlanningContractUpdateRequest plcUpdateRequest : request.getPlanningContracts()) {
                PlanningContract planningContract = (plcUpdateRequest.getId() != null ? planningContractsGroupById.get(plcUpdateRequest.getId()).toBuilder() : new PlanningContract().toBuilder())
                        .planning(planning)
                        .contract(contractsGroupById.get(plcUpdateRequest.getContractId()))
                        .customer(customersGroupById.get(plcUpdateRequest.getCustomerId()))
                        .timeReceived(plcUpdateRequest.getTimeReceived())
                        .timeDelivery(plcUpdateRequest.getTimeDelivery())
                        .bagNumber(plcUpdateRequest.getBagNumber())
                        .numberOfLooseBags(plcUpdateRequest.getNumberOfLooseBags())
                        .note(plcUpdateRequest.getNote())
                        .washingTeam(plcUpdateRequest.getWashingTeam())
                        .productType(productTypeRepository.findByValue(plcUpdateRequest.getProductTypeValue()).orElseThrow(() -> new AppException(4041)))
                        .build();
                newPlanningContracts.add(planningContract);
            }
        }
        planning.getPlanningContracts().clear();
        planning.getPlanningContracts().addAll(newPlanningContracts);
        return planningRepository.save(planning);
    }

    public Planning updateStatus(Long planningId, String statusRequest) {
        Planning planning = planningRepository.findById(planningId).orElseThrow(() -> new AppException(4041));
        if (planning.getStatus() != PlanningStatus.PENDING) {
            throw new AppException(4101);
        }

        if (PlanningStatus.valueOf(statusRequest) == PlanningStatus.APPROVED) {
            if (planning.getFromDate().compareTo(
                    ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ebstUserRequest.currentZoneId()).toInstant()) < 0) {
                throw new AppException(4103);
            }

            List<PlanningContract> planningContracts = planning.getPlanningContracts();
            if (!planningContracts.isEmpty()) {
                for (PlanningContract planningContract : planningContracts) {
                    ReceivedReceiptCreateRequest receivedReceiptCreateRequest = new ReceivedReceiptCreateRequest();
                    receivedReceiptCreateRequest.setCustomerId(planningContract.getCustomer().getId());
                    receivedReceiptCreateRequest.setProductTypeValue(planningContract.getProductType().getValue());
                    receivedReceiptCreateRequest.setCreatedSourceType(CreatedSourceType.PORTAL.getCode());
                    receivedReceiptCreateRequest.setIsRewash(false);
                    receivedReceiptCreateRequest.setAutoGen(true);
                    receivedReceiptCreateRequest.setBagNumber(planningContract.getBagNumber());
                    receivedReceiptCreateRequest.setNote(planningContract.getNote());
                    receivedReceiptCreateRequest.setSignatureCustomer("{}");
                    receivedReceiptCreateRequest.setSignatureStaff("{}");
                    receivedReceiptCreateRequest.setReceivedDate(
                            planning.getFromDate().atZone(ebstUserRequest.currentZoneId())
                                    .plusHours(Long.parseLong(planningContract.getTimeReceived().substring(0, 2)))
                                    .plusMinutes(Long.parseLong(planningContract.getTimeReceived().substring(3)))
                                    .withZoneSameInstant(ZoneId.of(UTC_ZONE_ID))
                                    .toInstant()
                    );
                    receivedReceiptCreateRequest.setDeliveryDate(
                            planning.getFromDate().atZone(ebstUserRequest.currentZoneId())
                                    .plusHours(Long.parseLong(planningContract.getTimeDelivery().substring(0, 2)))
                                    .plusMinutes(Long.parseLong(planningContract.getTimeDelivery().substring(3)))
                                    .withZoneSameInstant(ZoneId.of(UTC_ZONE_ID))
                                    .toInstant()
                    );

                    // increase delivery date by one day if it is less than received date
                    if (receivedReceiptCreateRequest.getDeliveryDate().compareTo(receivedReceiptCreateRequest.getReceivedDate()) < 0) {
                        receivedReceiptCreateRequest.setDeliveryDate(receivedReceiptCreateRequest.getDeliveryDate().plus(1, ChronoUnit.DAYS));
                    }

                    // init item received of received receipt
                    List<ItemReceivedCreateRequest> itemReceivedCreateRequests = new ArrayList<>();
                    Contract contract = planningContract.getContract();
                    if (contract != null) {
                        receivedReceiptCreateRequest.setContractId(contract.getId());
                        for (ContractProduct contractProduct : contract.getContractProducts()) {
                            ItemReceivedCreateRequest itemReceivedCreateRequest = new ItemReceivedCreateRequest();
                            if (Objects.equals(
                                            contractProduct.getProductItem().getProductType().getValue(),
                                            planningContract.getProductType().getValue()
                            ) && !contractProduct.getProductItem().isOther()) {
                                itemReceivedCreateRequest.setProductItemId(contractProduct.getProductItem().getId());
                                if ("special_product".equals(contractProduct.getProductItem().getProductType().getValue())) {
                                    itemReceivedCreateRequest.setLaundryFormValue("laundry");
                                }
                                itemReceivedCreateRequests.add(itemReceivedCreateRequest);
                            }
                        }
                    }

                    receivedReceiptCreateRequest.setItemReceivedCreateRequests(itemReceivedCreateRequests);
                    receivedReceiptService.create(receivedReceiptCreateRequest);
                }
            }
        }

        planning.setStatus(PlanningStatus.valueOf(statusRequest));
        return planningRepository.save(planning);
    }

    @Transactional(readOnly = true)
    public Page<PlanningDto> findAll(PlanningSearchRequest request, Pageable pageable) {
//        return planningRepository.findAllBy(new PlanningQuerydsl(allParams).getPredicate(), pageable);
        return planningDAO.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public PlanningDto findById(Long id) {
//        return planningMapper.entityToDto(planningRepository.findById(id).orElseThrow(() -> new AppException(4041)));
        PlanningDto planningDto = planningDAO.findById(id);
        planningDto.setPlanningContracts(planningContractDAO.findByPlanningId(planningDto.getId()));
        return planningDto;
    }
}
