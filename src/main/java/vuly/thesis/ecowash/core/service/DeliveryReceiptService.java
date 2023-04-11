package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.document.generator.DeliveryReceiptGenerator;
import vuly.thesis.ecowash.core.entity.*;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;
import vuly.thesis.ecowash.core.entity.type.ReceiptStatus;
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.*;
import vuly.thesis.ecowash.core.payload.request.*;
import vuly.thesis.ecowash.core.repository.*;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.BriefDeliveryReceiptDtoDAO;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.DeliveryReceiptDAO;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.ItemDeliveryDtoDAO;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.NumberDeliveryReceiptDAO;
import vuly.thesis.ecowash.core.service.mail.MailService;
import vuly.thesis.ecowash.core.util.EbstUserRequest;
import vuly.thesis.ecowash.core.util.StringUtil;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DeliveryReceiptService {
    private final EbstUserRequest ebstUserRequest;
    private final DeliveryReceiptRepository deliveryReceiptRepository;
    private final CustomerService customerService;
    private final ProductTypeService productTypeService;
    private final ItemDeliveryRepository itemDeliveryRepository;
    private final ProductItemService productItemService;
    private final LaundryFormService laundryFormService;
    private final ReceivedReceiptRepository receivedReceiptRepository;
    private final DeliveryReceiptDAO deliveryReceiptDtoDAO;
    private final NumberDeliveryReceiptDAO numberDeliveryReceiptDAO;
    private final ItemDeliveryDtoDAO itemDeliveryDtoDAO;
    private final BriefDeliveryReceiptDtoDAO briefDeliveryReceiptDtoDAO;
    private final TruckRepository truckRepository;
    private final StaffRepository staffRepository;
    private final MailService mailService;
    private final DeliveryReceiptGenerator deliveryReceiptGenerator;
    private final DeliveryReceiptHistoryRepository receiptHistoryRepository;
    private final ReceivedReceiptHistoryRepository receivedReceiptHistoryRepository;

    public DeliveryReceipt create(DeliveryReceiptCreateRequest request, boolean isAutoGen, boolean isGenByDebt) {
        if(!request.isAutoGen() && !request.isGenByDebt()) {
            if (request.getReceivedIds() == null || request.getReceivedIds().isEmpty() ||
                    request.getDeliveryDate() == null || request.getCheckDate() == null ||
                    request.getStaffCheck() == null || request.getStaffCheck().isEmpty()) {
                throw new AppException(4023);
            }
            for (ItemDeliveryCreateRequest item : request.getItemDeliveryCreateRequests()) {
                if (item.getNumberAfterProduction() == null || item.getNumberDelivery() == null) {
                    throw new AppException(4023);
                }
            }
        }

        DeliveryReceipt deliveryReceipt = createNewReceipt(request);
        deliveryReceipt.setAutoGen(isAutoGen);
        deliveryReceipt.setGenByDebt(request.isGenByDebt());
        deliveryReceipt.setItemDeliveryList(initDataItemDelivery(request.getItemDeliveryCreateRequests(), deliveryReceipt));
        deliveryReceipt = addDetail(deliveryReceipt, request.getReceivedIds(),
                request.getImages(), request.getCreatedSourceType());
        deliveryReceiptRepository.save(deliveryReceipt);
        addReceiptHistory(deliveryReceipt, ReceiptStatus.WAITING);
        return deliveryReceipt;
    }

    DeliveryReceipt addDetail(DeliveryReceipt deliveryReceipt, List<Long> receivedIds,
                              List<String> images, String createdSourceType) {
//        List<SpecialInstructionOfReceipt> specialInstructionOfReceipts = new ArrayList<>();
//        if (specialInstructions != null) {
//            for (String value : specialInstructions) {
//                SpecialInstructionOfReceipt specialInstructionOfReceipt = SpecialInstructionOfReceipt.builder()
//                        .specialInstruction(specialInstructionService.getByValue(value))
//                        .deliveryReceipt(deliveryReceipt)
//                        .build();
//                specialInstructionOfReceipts.add(specialInstructionOfReceipt);
//            }
//        }

        List<DeliveryLinkReceived> deliveryLinkReceiptArrayList = new ArrayList<>();
        for (Long id : receivedIds) {
            ReceivedReceipt receipt = receivedReceiptRepository.findById(id).orElseThrow(()
                    -> new AppException(4041, new ArrayList<>().add(new String[]{"ReceivedReceipt" + id})));
            if (receipt.isExpress()) {
                deliveryReceipt.setExpress(true);
            }
            if (receipt.getStatus().equals(ReceiptStatus.DONE)) {
                List<Object> params = new ArrayList();
                params.add(receipt.getCode());
                throw new AppException(HttpStatus.BAD_REQUEST, 4300, params);
            }
            DeliveryLinkReceived deliveryLinkReceipt = DeliveryLinkReceived.builder()
                    .receivedReceipt(receipt)
                    .deliveryReceipt(deliveryReceipt)
                    .build();
            deliveryLinkReceiptArrayList.add(deliveryLinkReceipt);
        }

        List<ImageReceipt> imageReceipts = deliveryReceipt.getImageReceipts() != null ? deliveryReceipt.getImageReceipts() : new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (String img : images) {
                if (!checkImageExist(img, imageReceipts, CreatedSourceType.valueOf(createdSourceType))) {
                    ImageReceipt imageReceipt = ImageReceipt.builder()
                            .deliveryReceipt(deliveryReceipt)
                            .image(img)
                            .createdSourceType(CreatedSourceType.valueOf(createdSourceType))
                            .build();
                    imageReceipts.add(imageReceipt);
                }
            }
        }

//        if (deliveryReceipt.getSpecialInstructionOfReceipts() != null) {
//            deliveryReceipt.getSpecialInstructionOfReceipts().clear();
//            deliveryReceipt.getSpecialInstructionOfReceipts().addAll(specialInstructionOfReceipts);
//        } else {
//            deliveryReceipt.setSpecialInstructionOfReceipts(specialInstructionOfReceipts);
//        }

        if (deliveryReceipt.getDeliveryLinkReceivedList() != null) {
            deliveryReceipt.getDeliveryLinkReceivedList().clear();
            deliveryReceipt.getDeliveryLinkReceivedList().addAll(deliveryLinkReceiptArrayList);
        } else {
            deliveryReceipt.setDeliveryLinkReceivedList(deliveryLinkReceiptArrayList);
        }

        deliveryReceipt.setImageReceipts(imageReceipts);
        return deliveryReceipt;
    }

    DeliveryReceipt createNewReceipt(DeliveryReceiptCreateRequest request) {
        Customer customer = customerService.findById(request.getCustomerId());
        String deliveryDate = DateTimeFormatter.ofPattern("ddMMyy").withZone(ebstUserRequest.currentZoneId()).format(Instant.now());
        String code = "";
        int sequenceNumber = 1;

        Integer sequenceNumberOpt = deliveryReceiptRepository.findByLikeCodeAndMaxSequenceNumber(String.format("%s-%s", customer.getCode(), deliveryDate));
        if (sequenceNumberOpt != null) {
            code = String.format("%s-%s-%03dG", customer.getCode(), deliveryDate, sequenceNumberOpt + 1);
            sequenceNumber = sequenceNumberOpt + 1;
        } else {
            code = String.format("%s-%s-%03dG", customer.getCode(), deliveryDate, 1);
        }

        DeliveryReceipt.DeliveryReceiptBuilder deliveryReceiptBuilder = DeliveryReceipt
                .builder()
                .code(code)
                .customer(customer)
                .productType(productTypeService.getByValue(request.getProductTypeValue()))
                .deliveryDate(request.getDeliveryDate())
                .staffCheck(request.getStaffCheck())
                .checkDate(request.getCheckDate())
                .bagNumber(request.getBagNumber())
                .numberOfLooseBags(request.getNumberOfLooseBags())
                .weight(request.getWeight())
                .status(ReceiptStatus.WAITING)
                .note(request.getNote())
                .isAutoGen(request.isAutoGen())
                .isFlagError(false)
                .isExpress(request.isExpress())
                .isGenByDebt(request.isGenByDebt())
                .sequenceNumber(sequenceNumber)
                .numberRoom(request.getNumberRoom())
                .signatureStaff(request.getSignatureStaff())
                .signatureCustomer(request.getSignatureCustomer());

//        Optional<Staff> staffOpt = staffRepository.findByIdAndExistedSignature(securityService.getUser().getStaffId(), null);
//        log.info("user", securityService.getUser());
//        if (staffOpt.isPresent()) {
//            deliveryReceiptBuilder.signatureStaff(request.getSignatureStaff());
//        } else {
//            deliveryReceiptBuilder.signatureStaff(staffOpt.get().getSignature());
//        }

        if (request.getTruckId() > 0){
            deliveryReceiptBuilder.truck(truckRepository.findByIdAndActive(request.getTruckId()).orElseThrow(() -> new AppException(4402)));
        } else {
            deliveryReceiptBuilder.truck(null);
        }
        if (request.getDriverId() > 0){
            deliveryReceiptBuilder.driver(staffRepository.findByIdAndActive(request.getDriverId()).orElseThrow(() -> new AppException(4402)));
        } else {
            deliveryReceiptBuilder.driver(null);
        }

        return deliveryReceiptBuilder.build();
    }

    private List<ItemDelivery> initDataItemDelivery(List<ItemDeliveryCreateRequest> requests,
                                                    DeliveryReceipt deliveryReceipt) {
        return requests.stream().map(it -> {
            ReceivedReceipt receipt = receivedReceiptRepository.findById(it.getReceivedReceiptId()).orElseThrow(()
                    -> new AppException(4041, new ArrayList<>().add(new String[]{"ReceivedReceipt" + it.getReceivedReceiptId()})));
            ItemDelivery detail = (it.getId() != null ? itemDeliveryRepository.findById(it.getId())
                    .orElseThrow(() -> new AppException(4041, new ArrayList<>().add(new String[]{"ItemDelivery" + it.getId()}))).toBuilder() : ItemDelivery.builder())
                    .id(it.getId())
                    .productItem(productItemService.getProductItemById(it.getProductItemId()))
                    .numberReceived(it.getNumberReceived() != null ? it.getNumberReceived() : 0)
                    .numberDelivery(it.getNumberDelivery() != null ? it.getNumberDelivery() : 0)
                    .laundryForm(it.getLaundryFormValue() != null ? laundryFormService.getByValue(it.getLaundryFormValue()) : null)
                    .note(it.getNote())
                    .deliveryReceipt(deliveryReceipt)
                    .receivedReceipt(receipt)
                    .randomNumberCheck(it.getRandomNumberCheck() != null ? it.getRandomNumberCheck() : 0)
                    .numberAfterProduction(it.getNumberAfterProduction() != null ? it.getNumberAfterProduction() : 0)
                    .numberDeliveryActual(it.getNumberDeliveryActual() != null ? it.getNumberDeliveryActual() : 0)
                    .build();
            if (it.getId() != null) {
//                itemDeliveryRepository.save(detail);
                return detail;
            } else {
                return detail;
            }
        }).collect(Collectors.toList());
    }

    public Object update(Long receiptId, DeliveryReceiptUpdateRequest request) {
        if (request.getReceivedIds() == null || request.getReceivedIds().isEmpty() ||
                request.getDeliveryDate() == null || request.getCheckDate() == null ||
                request.getStaffCheck() == null || request.getStaffCheck().isEmpty()) {
            throw new AppException(4023);
        }
        for (ItemDeliveryCreateRequest item : request.getItemDeliveryList()) {
            if (item.getNumberAfterProduction() == null || item.getNumberDelivery() == null) {
                throw new AppException(4023);
            }
        }
        Optional<DeliveryReceipt> deliveryReceipt = deliveryReceiptRepository.findById(receiptId);
        if (deliveryReceipt.isPresent()) {
            DeliveryReceipt nDeliveryReceipt = deliveryReceipt.get();

            if (nDeliveryReceipt.getStatus() == ReceiptStatus.DONE) {
                throw new AppException(4303);
            }

            nDeliveryReceipt.setDeliveryDate(request.getDeliveryDate());
            nDeliveryReceipt.setCheckDate(request.getCheckDate());
            nDeliveryReceipt.setBagNumber(request.getBagNumber()!= null ? request.getBagNumber() : 0);
            nDeliveryReceipt.setNote(request.getNote());
            nDeliveryReceipt.setWeight(request.getWeight());
            nDeliveryReceipt.setNumberOfLooseBags(request.getNumberOfLooseBags());
            nDeliveryReceipt.setNumberRoom(request.getNumberRoom());
            nDeliveryReceipt.setStaffCheck(request.getStaffCheck());
            nDeliveryReceipt.setReceiptImage(request.getReceiptImage());

            List<ItemDelivery> itemDeliveryList = nDeliveryReceipt.getItemDeliveryList();
            List<ItemDelivery> nItemDeliveryList = new ArrayList<>();

            for (ItemDelivery itemDelivery : itemDeliveryList) {
                if (!checkItemNeedRemove(request.getItemDeliveryList(), itemDelivery.getId())) {
                    nItemDeliveryList.add(itemDelivery);
                }
            }

//            if (request.getSpecialInstructions().contains("express_service")) {
//                nDeliveryReceipt.setExpress(true);
//            } else {
//                nDeliveryReceipt.setExpress(false);
//            }

            nDeliveryReceipt.getItemDeliveryList().clear();
            nDeliveryReceipt.getItemDeliveryList().addAll(nItemDeliveryList);


            List<ItemDelivery> itemDeliveries = this.initDataItemDelivery(request.getItemDeliveryList(), nDeliveryReceipt);
            if (itemDeliveries != null) {
                nDeliveryReceipt.getItemDeliveryList().addAll(itemDeliveries);
            }

            if (request.getTruckId() > 0){
                nDeliveryReceipt.setTruck(truckRepository.findById(request.getTruckId()).orElse(null));
            } else {
                nDeliveryReceipt.setTruck(null);
            }
            if (request.getDriverId() > 0){
                nDeliveryReceipt.setDriver(staffRepository.findById(request.getDriverId()).orElse(null));
            } else {
                nDeliveryReceipt.setDriver(null);
            }

            nDeliveryReceipt.getDeliveryLinkReceivedList().clear();
//            nDeliveryReceipt.getSpecialInstructionOfReceipts().clear();
            nDeliveryReceipt = addDetail(nDeliveryReceipt, request.getReceivedIds(),
                    request.getImages(), request.getCreatedSourceType());

            return deliveryReceiptRepository.save(nDeliveryReceipt);
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"DeliveryReceipt" + receiptId}));
        }
    }

    boolean checkItemNeedRemove(List<ItemDeliveryCreateRequest> itemDeliveryCreateRequests, long id) {
        for (ItemDeliveryCreateRequest itemDeliveryCreateRequest : itemDeliveryCreateRequests) {
            if (itemDeliveryCreateRequest.getId() != null && itemDeliveryCreateRequest.getId() == id) {
                return false;
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Page<DeliveryReceiptDto> findAllReceipt(DeliveryReceiptSearchRequest request, Pageable pageable) {
        return deliveryReceiptDtoDAO.findAll(request, pageable);
    }

    public DeliveryReceipt getReceipt(Long id) {
        Optional<DeliveryReceipt> deliveryReceipt = deliveryReceiptRepository.findById(id);

        if (deliveryReceipt.isPresent()) {
            return deliveryReceipt.get();
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"DeliveryReceipt" + id}));
        }
    }

    public DeliveryReceipt updateStatus(Long id, ReceiptStatusRequest status) {
        Optional<DeliveryReceipt> deliveryReceipt = deliveryReceiptRepository.findById(id);
        if (deliveryReceipt.isPresent()) {
            DeliveryReceipt receipt = deliveryReceipt.get();
            Optional<DeliveryReceipt> checkDoneReceipt = deliveryReceiptRepository.checkExistedReceivedReceiptDone(id);
            if (checkDoneReceipt.isPresent() && !receipt.getStatus().equals(ReceiptStatus.WAITING)) {
                for (DeliveryLinkReceived item : checkDoneReceipt.get().getDeliveryLinkReceivedList()) {
                    List<Object> params = new ArrayList<>();
                    params.add(item.getReceivedReceipt().getCode());
                    throw new AppException(HttpStatus.BAD_REQUEST, 4300, params);
                }
            }
            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.CANCEL && receipt.getStatus().equals(ReceiptStatus.WAITING)) {
                receipt.setFlagError(false);
                receipt.setStatus(ReceiptStatus.CANCEL);
                if (status.getCancelNote() != null && !status.getCancelNote().isEmpty()) {
                    receipt.setCancelNote(status.getCancelNote());
                }
            }

            if (receipt.getStatus() == ReceiptStatus.DONE) {
                throw new AppException(4303);
            }

            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.DONE) {
                checkErrorReceipt(id);
                if (receipt.getReceiptImage() != null && !receipt.getReceiptImage().equals("")) {
                    receipt.setStatus(ReceiptStatus.DONE);
                } else {
                    throw new AppException(4311);
                }
            } else {
                throw new AppException(4003);
            }

            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.WAITING_DELIVERY
                    || ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.DELIVERY) {
            }

            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.DELIVERY) {
                if (receipt.getTruck() == null || receipt.getDriver() == null) {
                    throw new AppException(4401);
                }
                if (receipt.getTruck().getStatus() == Status.DEACTIVE || receipt.getDriver().getStatus() == Status.DEACTIVE) {
                    throw new AppException(4402);
                }

                for (ItemDelivery item : deliveryReceipt.get().getItemDeliveryList()) {
                    item.setNumberDeliveryActual(item.getNumberDelivery());
                }
            }

            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.WAITING_RANDOM_CHECK) {
                if (receipt.getBagNumber() == 0) {
                    throw new AppException(4308);
                }
                receipt.setStatus(ReceiptStatus.WAITING_RANDOM_CHECK);
            } else {
                throw new AppException(4003);
            }

            receipt.setStatus(ReceiptStatus.valueOf(status.getStatus()));

            deliveryReceiptRepository.save(receipt);
            addReceiptHistory(receipt, receipt.getStatus());
            for (DeliveryLinkReceived item : receipt.getDeliveryLinkReceivedList()) {
                ReceivedReceipt receivedReceipt = item.getReceivedReceipt();
                if (checkReceivedReceiptHasDone(item.getReceivedReceipt())) {
                    receivedReceipt.setStatus(ReceiptStatus.DONE);
                    receivedReceiptRepository.save(receivedReceipt);
                    ReceivedReceiptHistory receiptHistory = ReceivedReceiptHistory
                            .builder()
                            .receivedId(receivedReceipt.getId())
                            .code(receivedReceipt.getCode())
                            .status(ReceiptStatus.DONE)
                            .build();
                    receivedReceiptHistoryRepository.save(receiptHistory);
                } else if (ReceiptStatus.valueOf(status.getStatus()).equals(ReceiptStatus.DONE) && !receipt.isFlagError()) {
                    receivedReceipt.setHasDeliveryReceiptDone(true);
                    receivedReceiptRepository.save(receivedReceipt);
                }
            }
            return receipt;
        }else {throw new AppException(4041, new ArrayList<>().add(new String[]{"DeliveryReceipt" + id}));}
    }


    public DeliveryReceipt confirmNoError(Long id, ReceiptConfirmErrorRequest request) {
        Optional<DeliveryReceipt> deliveryReceipt = deliveryReceiptRepository.findById(id);

        if (deliveryReceipt.isPresent()) {
            DeliveryReceipt receipt = deliveryReceipt.get();
            receipt.setConfirmNote(request.getNote());
            receipt.setFlagError(false);
            deliveryReceiptRepository.saveAndFlush(receipt);
            for (DeliveryLinkReceived item : receipt.getDeliveryLinkReceivedList()) {
                ReceivedReceipt receivedReceipt = item.getReceivedReceipt();
                if (checkReceivedReceiptHasDone(item.getReceivedReceipt())) {
                    receivedReceipt.setStatus(ReceiptStatus.DONE);
                    receivedReceipt.setFinishDate(Instant.now());
                    receivedReceiptRepository.save(receivedReceipt);
                } else if (receipt.getStatus().equals(ReceiptStatus.DONE)) {
                    receivedReceipt.setHasDeliveryReceiptDone(true);
                    receivedReceiptRepository.save(receivedReceipt);
                }
            }
            deliveryReceiptRepository.save(receipt);
            addReceiptHistory(receipt, ReceiptStatus.ACCEPT);
            return receipt;
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"DeliveryReceipt" + id}));
        }
    }

    public DeliveryReceipt updateImages(Long id, ArrayList<String> images, CreatedSourceType createdSourceType) {
        Optional<DeliveryReceipt> deliveryReceipt = deliveryReceiptRepository.findById(id);

        if (deliveryReceipt.isPresent()) {
            DeliveryReceipt receipt = deliveryReceipt.get();
            List<ImageReceipt> imageArr = receipt.getImageReceipts();
            for (String img : images) {
                if (!checkImageExist(img, imageArr, createdSourceType)) {
                    ImageReceipt imageReceipt = ImageReceipt.builder()
                            .deliveryReceipt(receipt)
                            .image(img)
                            .createdSourceType(createdSourceType)
                            .build();
                    imageArr.add(imageReceipt);
                }
            }

            receipt.setImageReceipts(imageArr);
            return deliveryReceiptRepository.save(receipt);
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"DeliveryReceipt" + id}));
        }
    }

    public DeliveryReceipt deleteImages(Long id, List<ImageReceipt> imageReceiptRemoveList) {
        DeliveryReceipt deliveryReceipt = deliveryReceiptRepository.findById(id).orElseThrow(()
                -> new AppException(4041, new ArrayList<>().add(new String[]{"DeliveryReceipt" + id})));
        List<ImageReceipt> imageReceiptList = deliveryReceipt.getImageReceipts();

        for (ImageReceipt imageReceiptRemoved : imageReceiptRemoveList) {
            imageReceiptList.remove(imageReceiptRemoved);
            imageReceiptRemoved.setDeliveryReceipt(null);
        }
        return deliveryReceiptRepository.save(deliveryReceipt);
    }

    boolean checkImageExist(String imageName, List<ImageReceipt> imageArr, CreatedSourceType createdSourceType) {
        for (ImageReceipt image : imageArr) {
            if (image.getImage().equalsIgnoreCase(imageName)
                    && image.getCreatedSourceType().equals(createdSourceType)) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Page<NumberDeliveryReceiptDto> findNumberDeliveryOfReceipt(NumberDeliveryReceiptSearchRequest request, Pageable pageable) {
        if (request.getDeliveryId() != null) {
            return numberDeliveryReceiptDAO.findAll(request, pageable);
        }
        return numberDeliveryReceiptDAO.findAllNotDeliveryReceiptId(request, pageable);
    }

    List<ItemDelivery> findItemDeliveryByReceivedId(long receivedId, List<ItemDelivery> itemDeliveryList) {
        List<ItemDelivery> arr = new ArrayList<>();
        for (ItemDelivery itemDelivery : itemDeliveryList) {
            if (itemDelivery.getReceivedReceipt().getId().equals(receivedId)) {
                arr.add(itemDelivery);
            }
        }
        return arr;
    }

    public Object getDetailById(Long receiptId) {
        Optional<DeliveryReceipt> deliveryReceipt = deliveryReceiptRepository.findById(receiptId);

        if (deliveryReceipt.isPresent()) {
            DeliveryReceipt receipt = deliveryReceipt.get();
            Long totalDelivery = deliveryReceiptRepository.getTotalDeliveryByReceiptId(receipt.getId());
//            List<SpecialInstructionOfReceipt> specialInstructions = receipt.getSpecialInstructionOfReceipts();
//            StringBuilder specialStr = new StringBuilder();
//            for (SpecialInstructionOfReceipt item : specialInstructions) {
//                specialStr.append(item.getSpecialInstruction().getName()).append(",");
//            }
            List<String> imgOfDeliveries = new ArrayList<>();
            for (ImageReceipt item3 : receipt.getImageReceipts()) {
                imgOfDeliveries.add(item3.getImage());
            }
            List<ReceivedOfDReceiptData> receivedOfDReceiptDataList = new ArrayList<>();
            for (DeliveryLinkReceived item : receipt.getDeliveryLinkReceivedList()) {
                ReceivedReceipt receivedReceipt = item.getReceivedReceipt();

                StringBuilder specialStr1 = new StringBuilder();
                for (SpecialInstructionOfReceipt item1 : receivedReceipt.getSpecialInstructionOfReceipts()) {
                    specialStr1.append(item1.getSpecialInstruction().getName()).append(",");
                }

                List<String> imgs = new ArrayList<>();
                for (ImageReceipt item2 : receivedReceipt.getImageReceipts()) {
                    imgs.add(item2.getImage());
                }

                List<ItemDeliveryReceiptDto> itemDeliveryReceiptDtos = new ArrayList<>();
                List<ItemDelivery> itemDeliveryList = findItemDeliveryByReceivedId(receivedReceipt.getId(), receipt.getItemDeliveryList());
                for (ItemDelivery itemDelivery : itemDeliveryList) {
                    ItemDeliveryReceiptDto.ItemDeliveryReceiptDtoBuilder it1 = ItemDeliveryReceiptDto.builder()
                            .productItemName(itemDelivery.getProductItem().getName())
                            .numberDeliveryActual(itemDelivery.getNumberDeliveryActual())
                            .numberReceived(itemDelivery.getNumberReceived())
                            .pieceTypeValue(itemDelivery.getProductItem().getPieceType().getValue())
                            .laundryForm(itemDelivery.getLaundryForm() != null ? itemDelivery.getLaundryForm().getName() : "")
                            .note(StringUtil.isEmpty(itemDelivery.getNote()) ? "" : itemDelivery.getNote());
                    itemDeliveryReceiptDtos.add(it1.build());
                }

                ReceivedOfDReceiptData.ReceivedOfDReceiptDataBuilder receiptDataBuilder
                        = ReceivedOfDReceiptData.builder()
                        .numberRoom(receivedReceipt.getNumberRoom())
                        .code(receivedReceipt.getCode())
                        .productTypeValue(receivedReceipt.getProductType().getValue())
                        .specialInstructions(specialStr1.length() > 0 ? specialStr1.subSequence(0, specialStr1.length() - 1).toString() : "")
                        .images(imgs)
                        .itemDeliveryList(itemDeliveryReceiptDtos);
                receivedOfDReceiptDataList.add(receiptDataBuilder.build());
            }

            DeliveryReceiptDetailData.DeliveryReceiptDetailDataBuilder data = DeliveryReceiptDetailData.builder()
                    .code(receipt.getCode())
                    .productTypeName(receipt.getProductType().getName())
                    .productTypeValue(receipt.getProductType().getValue())
                    .customerName(receipt.getCustomer().getFullName())
                    .deliveryDate(receipt.getDeliveryDate())
                    .note(StringUtil.isEmpty(receipt.getNote()) ? "" : receipt.getNote())
                    .bagNumber(receipt.getBagNumber())
                    .numberRoom(receipt.getNumberRoom())
//                    .specialInstructions(specialStr.length() > 0 ? specialStr.subSequence(0, specialStr.length() - 1).toString() : "")
                    .receivedOfDReceiptDataList(receivedOfDReceiptDataList)
                    .signatureStaff(receipt.getSignatureStaff())
                    .signatureCustomer(receipt.getSignatureCustomer())
                    .images(imgOfDeliveries)
                    .status(receipt.getStatus().getCode());

            return data.build();
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"DeliveryReceipt" + receiptId}));
        }
    }

//    @Transactional(readOnly = true)
//    public DeliveryReceiptDto findById(Long id) {
//        List<ItemDeliveryDto> itemDeliveryDtoList = itemDeliveryDtoDAO.findByDeliveryReceiptId(id, null);
////        List<String> specialInstructionList = specialInstructionRepository.findByDeliveryReceiptId(id);
//        DeliveryReceiptDto deliveryReceiptDto = deliveryReceiptDtoDAO.findById(id);
//        deliveryReceiptDto.setItemDeliveryList(itemDeliveryDtoList);
//        deliveryReceiptDto.setReceivedDate(itemDeliveryDtoList.get(0).getReceivedDate());
////        deliveryReceiptDto.setSpecialInstructionsList(specialInstructionList);
//        deliveryReceiptDto.setReceivedIds(itemDeliveryDtoList.stream().map(ItemDeliveryDto::getReceivedReceiptId).collect(Collectors.toList()));
//        return deliveryReceiptDto;
//    }

    public DeliveryReceipt updateDeliveryConfirm(Long id, UpdateDeliveryActualRequest updateDeliveryActualRequest) {
        Optional<DeliveryReceipt> deliveryReceiptOpt = deliveryReceiptRepository.findById(id);
        DeliveryReceipt deliveryReceipt = deliveryReceiptOpt.get();
        for (ItemDelivery itemDelivery : deliveryReceipt.getItemDeliveryList()) {
            for (ItemDeliveryUpdateRequest request : updateDeliveryActualRequest.getItemDeliveryUpdateRequests()) {
                if (itemDelivery.getId().equals(request.getId())) {
                    if (request.getNumberDeliveryActual() != itemDelivery.getNumberDelivery()) {
                        deliveryReceipt.setFlagError(true);
                        deliveryReceipt.setMarkError(true);
                    }
                    itemDelivery.setNumberDeliveryActual(request.getNumberDeliveryActual());
                }
            }
        }
        return deliveryReceiptRepository.save(deliveryReceipt);
    }


    @Transactional(readOnly = true)
    public List<BriefDeliveryReceiptDto> findAllBriefReceipt(BriefDeliveryReceiptSearchRequest request) {
        List<BriefDeliveryReceiptDto> result = new ArrayList<>();
        List<BriefDeliveryReceiptDto> deliveryReceiptList = briefDeliveryReceiptDtoDAO.getBriefReceiptList(request);
        for (BriefDeliveryReceiptDto deliveryReceipt : deliveryReceiptList) {
            List<ItemDeliveryDto> itemDeliveryDtoList = itemDeliveryDtoDAO.findByDeliveryReceiptId(deliveryReceipt.getId(), request.getProductItemName());
            BriefDeliveryReceiptDto briefDeliveryReceiptDto = new BriefDeliveryReceiptDto();
            briefDeliveryReceiptDto.setId(deliveryReceipt.getId());
            briefDeliveryReceiptDto.setTenantId(deliveryReceipt.getTenantId());
            briefDeliveryReceiptDto.setCode(deliveryReceipt.getCode());
            briefDeliveryReceiptDto.setItemDeliveryList(itemDeliveryDtoList);
            briefDeliveryReceiptDto.setCustomerId(deliveryReceipt.getCustomerId());
            briefDeliveryReceiptDto.setProductTypeValue(deliveryReceipt.getProductTypeValue());
            briefDeliveryReceiptDto.setStatus(deliveryReceipt.getStatus());
            result.add(briefDeliveryReceiptDto);
        }
        return result;
    }

    public boolean checkReceivedReceiptHasDone(ReceivedReceipt receipt) {
        Long totalDelivery = 0L;
        for (ItemReceived itemReceived : receipt.getItemReceivedList()) {
            totalDelivery = deliveryReceiptRepository.getTotalActualDeliveryByReceivedReceiptId(receipt.getId(), itemReceived.getProductItem().getId());
            if (totalDelivery == null || totalDelivery < 0)
                return false;
        }
        return true;
    }

    public List<BriefDataDto> findCodeLike(String code, String status, Pageable pageable) {
        return deliveryReceiptRepository.findCodeLike(code, status, pageable);
    }

    public void sendViaEmail(DeliveryReceipt deliveryReceipt) throws MessagingException, DocumentException, IOException, com.lowagie.text.DocumentException {
        if (deliveryReceipt.getStatus() == ReceiptStatus.DONE
                && !"{}".equals(deliveryReceipt.getSignatureStaff())
                && !"{}".equals(deliveryReceipt.getSignatureCustomer())
        ) {
            List<String> emails = staffRepository.findByTenantIdAndCustomerIdAAndIsCustomer(deliveryReceipt.getCustomer().getId(), true);
            if (emails.isEmpty()) {
                return;
            }
            String deliveryDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .withZone(ebstUserRequest.currentZoneId())
                    .format(deliveryReceipt.getDeliveryDate());
            String content = "<div style='line-height: 1.8; font-size: 13px;'>" +
                    "        <p>" +
                    "            ECOWASH HCMC xin thông báo đến quý khách hàng: " +
                    "            <br/>" +
                    "            Phiếu hàng <strong>" + deliveryReceipt.getCode() + "</strong> đã được giao hàng thành công vào ngày " + deliveryDate + ". " +
                    "            Để xem thông tin chi tiết phiếu hàng, quý khách vui lòng xem tại file đính kèm bên dưới email." +
                    "            <br/>" +
                    "            Nếu cần hỗ trợ thêm thông tin nào, quý khách vui lòng liên hệ hotline: 1900 9090" +
                    "            <br/>" +
                    "            Xin cảm ơn!" +
                    "            <br/>" +
                    "            ECOWASH" +
                    "            <br/> " +
                    "        </p>" +
                    "        <img src='https://ecowash.net.vn/storage/logo-a-member-of-ebs-01.png' style='height: 100px;'/>" +
                    "    </div>";
            mailService.sendWithAttachments(
                    emails,
                    "ECOWASH HCMC thông báo giao hàng thành công - " + deliveryReceipt.getCode(),
                    content,
                    deliveryReceipt.getCode() + ".pdf",
                    List.of(deliveryReceiptGenerator.generatePdfFile(deliveryReceipt))
            );
        }
    }

    public void checkErrorReceipt(Long id) {
        Optional<DeliveryReceipt> deliveryReceipt = deliveryReceiptRepository.findById(id);
        if (deliveryReceipt.isPresent()) {
            DeliveryReceipt delivery = deliveryReceipt.get();
            for (ItemDelivery item : delivery.getItemDeliveryList()) {
                if (item.getNumberDeliveryActual() != item.getNumberDelivery()) {
                    delivery.setFlagError(true);
                }
            }
            deliveryReceiptRepository.save(delivery);
        }
    }

    public void addReceiptHistory(DeliveryReceipt deliveryReceipt, ReceiptStatus status) {
        DeliveryReceiptHistory receiptHistory = DeliveryReceiptHistory
                .builder()
                .deliveryId(deliveryReceipt.getId())
                .code(deliveryReceipt.getCode())
                .status(status)
                .build();
        receiptHistoryRepository.save(receiptHistory);
    }
}
