package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.document.generator.ReceivedReceiptGenerator;
import vuly.thesis.ecowash.core.entity.*;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;
import vuly.thesis.ecowash.core.entity.type.ReceiptStatus;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;
import vuly.thesis.ecowash.core.payload.dto.ReceivedReceiptDto;
import vuly.thesis.ecowash.core.payload.request.*;
import vuly.thesis.ecowash.core.repository.*;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.GetDeliveryCodeDAO;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.ReceivedReceiptDtoDAO;
import vuly.thesis.ecowash.core.service.mail.MailService;
import vuly.thesis.ecowash.core.util.DateTimeUtil;
import vuly.thesis.ecowash.core.util.EbstUserRequest;
import vuly.thesis.ecowash.core.util.StringUtil;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReceivedReceiptService {
    private final EbstUserRequest ebstUserRequest;
    private final ReceivedReceiptRepository receivedReceiptRepository;
    private final ItemReceivedRepository itemReceivedRepository;
    private final ProductTypeService productTypeService;
    private final ProductItemService productItemService;
    private final ContractService contractService;
    private final TruckRepository truckRepository;
    private final LaundryFormService laundryFormService;
    private final CustomerService customerService;
    private final ReceivedReceiptDtoDAO receivedReceiptDtoDAO;
    private final SpecialInstructionService specialInstructionService;
    private final DeliveryReceiptRepository deliveryReceiptRepository;
    private final DeliveryReceiptService deliveryReceiptService;
    private final StaffRepository staffRepository;
    private final MailService mailService;
    private final ReceivedReceiptGenerator receivedReceiptGenerator;
    private final GetDeliveryCodeDAO getDeliveryCodeDAO;
    private final ReceivedReceiptHistoryRepository receiptHistoryRepository;

    public ReceivedReceipt create(ReceivedReceiptCreateRequest request) {
        ReceivedReceipt receivedReceipt = createNewReceipt(request);
        receivedReceipt.setItemReceivedList(initDataItemReceived(request.getItemReceivedCreateRequests(), receivedReceipt));
        receivedReceipt = addDetail(receivedReceipt,request.getSpecialInstruction(), request.getDeliveryIds(),
                request.getImages(), request.getCreatedSourceType());
        receivedReceiptRepository.save(receivedReceipt);
        addReceiptHistory(receivedReceipt, receivedReceipt.getStatus());
        return receivedReceipt;
    }

    public ReceivedReceipt addDetail(ReceivedReceipt receivedReceipt, List<String> specialInstructions,
                                     List<Long> deliveryIds, List<String> images, String createdSourceType){
        List<SpecialInstructionOfReceipt> specialInstructionOfReceipts = new ArrayList<>();

        if(specialInstructions != null && !specialInstructions.isEmpty()) {
            for (String value : specialInstructions) {
                SpecialInstructionOfReceipt specialInstructionOfReceipt = SpecialInstructionOfReceipt.builder()
                        .specialInstruction(specialInstructionService.getByValue(value))
                        .receivedReceipt(receivedReceipt)
//                        .customer(receivedReceipt.getCustomer())
                        .build();
                specialInstructionOfReceipts.add(specialInstructionOfReceipt);
            }
        }

        List<ReceivedLinkDelivery> deliveryLinkReceiptArrayList = new ArrayList<>();
        if(deliveryIds != null && !deliveryIds.isEmpty()){
            for (Long id : deliveryIds) {
                ReceivedLinkDelivery deliveryLinkReceipt = ReceivedLinkDelivery.builder()
                        .deliveryReceipt(findDataById(id))
                        .receivedReceipt(receivedReceipt)
                        .build();
                deliveryLinkReceiptArrayList.add(deliveryLinkReceipt);
            }
        }

        List<ImageReceipt> imageReceipts = receivedReceipt.getImageReceipts() != null ? receivedReceipt.getImageReceipts() : new ArrayList<>();
        if(images != null && !images.isEmpty()){
            for (String img : images) {
                if(!checkImageExist(img, imageReceipts, CreatedSourceType.valueOf(createdSourceType))){
                    ImageReceipt imageReceipt = ImageReceipt.builder()
                            .receivedReceipt(receivedReceipt)
                            .image(img)
                            .createdSourceType(CreatedSourceType.valueOf(createdSourceType))
                            .build();
                    imageReceipts.add(imageReceipt);
                }
            }
        }

        if(receivedReceipt.getSpecialInstructionOfReceipts() != null){
            receivedReceipt.getSpecialInstructionOfReceipts().clear();
            receivedReceipt.getSpecialInstructionOfReceipts().addAll(specialInstructionOfReceipts);
        }else{
            receivedReceipt.setSpecialInstructionOfReceipts(specialInstructionOfReceipts);
        }

        if(receivedReceipt.getReceivedLinkDeliveries() != null){
            receivedReceipt.getReceivedLinkDeliveries().clear();
            receivedReceipt.getReceivedLinkDeliveries().addAll(deliveryLinkReceiptArrayList);
        }else{
            receivedReceipt.setReceivedLinkDeliveries(deliveryLinkReceiptArrayList);
        }


        receivedReceipt.setImageReceipts(imageReceipts);
        return receivedReceipt;
    }

    public ReceivedReceipt createNewReceipt(ReceivedReceiptCreateRequest request) {
        String receivedDate = DateTimeFormatter.ofPattern("ddMMyy").withZone(ebstUserRequest.currentZoneId()).format(Instant.now());
        String code = "";
        int sequenceNumber = 1;
        ReceiptStatus status = ReceiptStatus.WAITING;

        Customer customer = customerService.findById(request.getCustomerId());
        if (!customer.getActive()) {
            throw new AppException(4203);
        }

        Integer sequenceNumberOpt = receivedReceiptRepository.findByLikeCodeAndMaxSequenceNumber(String.format("%s-%s", customer.getCode(), receivedDate));
        if (sequenceNumberOpt != null) {
            code = String.format("%s-%s-%03dN", customer.getCode(), receivedDate, sequenceNumberOpt + 1);
            sequenceNumber = sequenceNumberOpt + 1;
        } else {
            code = String.format("%s-%s-%03dN", customer.getCode(), receivedDate, 1);
        }

//        if (request.getSignatureCustomer() != null && !request.getSignatureCustomer().isEmpty() && !Objects.equals(request.getSignatureCustomer(), "{}")) {
//            status = ReceiptStatus.STAFF_RECEIVED;
//        }

        ReceivedReceipt.ReceivedReceiptBuilder receivedReceiptBuilder = ReceivedReceipt
                .builder()
                .code(code)
                .referenceCode(request.getReferenceCode())
                .sequenceNumber(sequenceNumber)
                .customer(customer)
                .productType(productTypeService.getByValue(request.getProductTypeValue()))
                .receivedDate(request.getReceivedDate())
                .deliveryDate(request.getDeliveryDate()!= null ? request.getDeliveryDate() : null)
                .isRewash(request.getIsRewash())
                .note(request.getNote())
                .numberRoom(request.getNumberRoom())
                .status(status)
                .bagNumber(request.getBagNumber() != null ? request.getBagNumber() : 0)
                .numberOfCheckBags(request.getBagNumber() != null ? request.getBagNumber() : 0)
                .weight(request.getWeight())
                .signatureCustomer(request.getSignatureCustomer())
                .isAutoGen(request.isAutoGen())
                .isFlagError(false)
                .imagePaths(request.getImagePaths())
                .createdSourceType(CreatedSourceType.valueOf(request.getCreatedSourceType()))
                .numberOfLooseBags(request.getNumberOfLooseBags());
        if (request.getContractId() > 0){
            if (contractService.findById(request.getContractId()).getExpiredDate().compareTo(Instant.now()) > 0 && request.getIsRewash() != null) {
                receivedReceiptBuilder.contract(contractService.findById(request.getContractId()));
            } else {
                throw new AppException(4109);
            }
        } else {
            receivedReceiptBuilder.contract(null);
        }
        if (request.getTruckId() > 0){
                receivedReceiptBuilder.truck(truckRepository.findByIdAndActive(request.getTruckId()).orElseThrow(() -> new AppException(4402)));
        }
        if (request.getDriverId() > 0){
            receivedReceiptBuilder.driver(staffRepository.findByIdAndActive(request.getDriverId()).orElseThrow(() -> new AppException(4402)));
        }

        if (request.getReceiptImage() != null) {
            receivedReceiptBuilder.receiptImage(request.getReceiptImage());
        }

        if(getStatusByCustomerSignature(request.getSignatureCustomer()).equals(ReceiptStatus.STAFF_RECEIVED)) {
            Optional<Staff> staffOpt = staffRepository.findById(securityService.getUser().getStaffId());
            receivedReceiptBuilder.status(ReceiptStatus.STAFF_RECEIVED);
            if (staffOpt.isPresent() && (staffOpt.get().getSignature() == null || staffOpt.get().getSignature().equals("{}"))) {
                receivedReceiptBuilder.signatureStaff(request.getSignatureStaff());
            } else {
                String signatureTime = DateTimeFormatter.ofPattern(DateTimeUtil.JSON_DATETIME_PATTERN_FAT_GUY)
                        .withZone(ebstUserRequest.currentZoneId()).format(Instant.now());
                JSONObject json = new JSONObject(staffOpt.get().getSignature());
                json.put("signature_time", signatureTime);
                receivedReceiptBuilder.signatureStaff(json.toString());
            }
        }

        if (request.getSpecialInstruction().contains("express_service")) {
            receivedReceiptBuilder.isExpress(true);
        }
        return receivedReceiptBuilder.build();
    }

    private ReceiptStatus getStatusByCustomerSignature(String customerSignatureURL){
        return customerSignatureURL != null && !customerSignatureURL.isEmpty() && !customerSignatureURL.equals("{}")
                ? ReceiptStatus.STAFF_RECEIVED
                : ReceiptStatus.WAITING;
    }

    private List<ItemReceived> initDataItemReceived(List<ItemReceivedCreateRequest> requests,
                                                          ReceivedReceipt receivedReceipt) {
        return requests.stream().map(it -> {
            DeliveryReceipt receipt = null;
            if(it.getDeliveryReceiptId() != null){
                receipt = deliveryReceiptRepository.findById(it.getDeliveryReceiptId()).orElseThrow(()
                        -> new AppException(4041));
            }

            ItemReceived detail = (it.getId() != null ? itemReceivedRepository.findById(it.getId())
                    .orElseThrow(() -> new AppException(4041)).toBuilder() : ItemReceived.builder())
                    .id(it.getId())
                    .productItem(productItemService.getProductItemById(it.getProductItemId()))
                    .numberReceived(it.getNumberReceived() != null ? it.getNumberReceived() : 0)
                    .randomNumberCheck(it.getRandomNumberCheck() != null ? it.getRandomNumberCheck() : 0)
                    .numberAfterProduction(it.getNumberAfterProduction() != null ? it.getNumberAfterProduction() : 0)
                    .note(it.getNote())
                    .receivedReceipt(receivedReceipt)
                    .deliveryReceipt(receipt)
                    .build();

            if(it.getLaundryFormValue() != null){
                detail.setLaundryForm(laundryFormService.getByValue(it.getLaundryFormValue()));
            }
            if(it.getId() != null){
                itemReceivedRepository.save(detail);
                return null;
            }else{
                return detail;
            }
        }).collect(Collectors.toList());
    }

    public ReceivedReceipt update(Long receiptId, ReceivedReceiptUpdateRequest request, CreatedSourceType createdSourceType) throws MessagingException {
        log.info("update Received Receipt {} {}", receiptId, request);
        ReceivedReceipt receivedReceipt = receivedReceiptRepository.findById(receiptId).orElseThrow(() -> new AppException(4041));
        if (receivedReceipt.getStatus().equals(ReceiptStatus.DONE) || receivedReceipt.getStatus().equals(ReceiptStatus.CANCEL)) {
            throw new AppException(4303);
        }

        if (receivedReceipt.getStatus().equals(ReceiptStatus.WAITING) && receivedReceipt.getContract() != null &&
                receivedReceipt.getContract().getExpiredDate().compareTo(Instant.now()) < 0 && !receivedReceipt.isFlagError()) {
            throw new AppException(4109);
        }

        if (request.getReceiptImage() != null && receivedReceipt.getStatus().equals(ReceiptStatus.WAITING)) {
            receivedReceipt.setReceiptImage(request.getReceiptImage());
        }

        if(request.getReceivedDate() != null)
            receivedReceipt.setReceivedDate(request.getReceivedDate());

        if(request.getDeliveryDate() != null)
            receivedReceipt.setDeliveryDate(request.getDeliveryDate());

        receivedReceipt.setReferenceCode(request.getReferenceCode());
        receivedReceipt.setNumberOfCheckBags(request.getNumberOfCheckBags() != null ? request.getNumberOfCheckBags() : receivedReceipt.getNumberOfCheckBags());
        receivedReceipt.setNote(request.getNote());
        receivedReceipt.setWeight(request.getWeight());
        receivedReceipt.setSignatureCustomer(request.getSignatureCustomer());
        receivedReceipt.setNumberOfLooseBags(StringUtil.isEmpty(request.getNumberOfLooseBags()) ? null : request.getNumberOfLooseBags());
        receivedReceipt.setDeliveryBagNumber(request.getDeliveryBagNumber() != null ? request.getDeliveryBagNumber() : 0);
        receivedReceipt.setNumberRoom(request.getNumberRoom());
        receivedReceipt.setImagePaths(request.getImagePaths());

        if(getStatusByCustomerSignature(request.getSignatureCustomer()).equals(ReceiptStatus.STAFF_RECEIVED) &&
                receivedReceipt.getStatus().equals(ReceiptStatus.WAITING)) {
            Optional<Staff> staffOpt = staffRepository.findById(securityService.getUser().getStaffId());
            receivedReceipt.setStatus(ReceiptStatus.STAFF_RECEIVED);
            if (staffOpt.isPresent() && (staffOpt.get().getSignature() == null || staffOpt.get().getSignature().equals("{}"))) {
                throw new AppException(4202);
            } else {
                String signatureTime = DateTimeFormatter.ofPattern(DateTimeUtil.JSON_DATETIME_PATTERN_FAT_GUY)
                        .withZone(ebstUserRequest.currentZoneId()).format(Instant.now());
                JSONObject json = new JSONObject(staffOpt.get().getSignature());
                json.put("signature_time", signatureTime);
                receivedReceipt.setSignatureStaff(json.toString());
            }
        }
//        if(request.getStatus() != null && !request.getStatus().isEmpty()){
//            receivedReceipt.setStatus(ReceiptStatus.valueOf(request.getStatus()));
//        }
        if (createdSourceType == CreatedSourceType.PORTAL) {
            if (request.getTruckId() > 0){
                receivedReceipt.setTruck(truckRepository.findByIdAndActive(request.getTruckId()).orElseThrow(() -> new AppException(4402)));
            } else {
                receivedReceipt.setTruck(null);
            }
            if (request.getDriverId() > 0){
                receivedReceipt.setDriver(staffRepository.findByIdAndActive(request.getDriverId()).orElseThrow(() -> new AppException(4402)));
            } else {
                receivedReceipt.setDriver(null);
            }
        }

        receivedReceipt.setExpress(request.getSpecialInstruction().contains("express_service"));

        List<ItemReceived> itemReceiveds = receivedReceipt.getItemReceivedList();
        List<ItemReceived> nItemReceiveds = new ArrayList<>();

        isFlagError(receivedReceipt, request);
        receivedReceipt.setBagNumber(request.getBagNumber()!= null ? request.getBagNumber() : 0);
        if (StringUtil.isNotEmpty(receivedReceipt.getSignatureStaff()) && !"{}".equals(receivedReceipt.getSignatureStaff())
                || StringUtil.isNotEmpty(receivedReceipt.getSignatureCustomer()) && !"{}".equals(receivedReceipt.getSignatureCustomer())) {
            receivedReceipt.setFlagError(false);
        }

        for(ItemReceived itemReceived: itemReceiveds){
            if(!checkItemNeedRemove(request.getItemReceivedCreateRequests(), itemReceived.getId())){
                nItemReceiveds.add(itemReceived);
            }
        }
        receivedReceipt.getItemReceivedList().clear();
        receivedReceipt.getItemReceivedList().addAll(nItemReceiveds);

        List<ItemReceived> itemReceivedList = this.initDataItemReceived(request.getItemReceivedCreateRequests(), receivedReceipt);
        for (ItemReceived item: itemReceivedList){
            if(item != null){
                receivedReceipt.getItemReceivedList().add(item);
            }
        }

        receivedReceipt.getReceivedLinkDeliveries().clear();
        receivedReceipt.getSpecialInstructionOfReceipts().clear();
        receivedReceipt = addDetail(receivedReceipt, request.getSpecialInstruction(), request.getDeliveryIds(), request.getImages(),
                request.getCreatedSourceType());
        // this.sendViaEmail(receivedReceipt);

        return receivedReceiptRepository.save(receivedReceipt);
    }

    public void sendViaEmail(ReceivedReceipt receivedReceipt) throws MessagingException, DocumentException, IOException, com.lowagie.text.DocumentException {
        if (receivedReceipt.getStatus() == ReceiptStatus.STAFF_RECEIVED
                && !"{}".equals(receivedReceipt.getSignatureStaff())
                && !"{}".equals(receivedReceipt.getSignatureCustomer())
        ) {
            List<String> emails = staffRepository.findByTenantIdAndCustomerIdAAndIsCustomer(receivedReceipt.getCustomer().getId(), true);
            if (emails.isEmpty()) {
                return;
            }
            String receivedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .withZone(ebstUserRequest.currentZoneId())
                    .format(receivedReceipt.getReceivedDate());
            String content = "<div style='line-height: 1.8; font-size: 13px;'>" +
                    "        <p>" +
                    "            ECOWASH HCMC xin thông báo đến quý khách hàng: " +
                    "            <br/>" +
                    "            Phiếu hàng <strong>" + receivedReceipt.getCode() + "</strong> đã được tiếp nhận thành công vào ngày " + receivedDate + ". " +
                    "               Để xem thông tin chi tiết phiếu hàng, quý khách vui lòng xem tại file đính kèm bên dưới email." +
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
                    "ECOWASH HCMC thông báo tiếp nhận phiếu hàng mới - " + receivedReceipt.getCode(),
                    content,
                    receivedReceipt.getCode() + ".pdf",
                    List.of(receivedReceiptGenerator.generatePdfFile(receivedReceipt))
            );
        }

    }

    boolean checkItemNeedRemove(List<ItemReceivedCreateRequest> itemReceivedList, long id){
        for(ItemReceivedCreateRequest itemReceived: itemReceivedList){
            if(itemReceived.getId() != null && itemReceived.getId() == id){
                return false;
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Page<ReceivedReceiptDto> findAllReceipt(ReceivedReceiptSearchRequest request, Pageable pageable) {
        return receivedReceiptDtoDAO.findAll(request, pageable);
    }

    public ReceivedReceipt getReceipt(Long id) {
        Optional<ReceivedReceipt> receivedReceipt = receivedReceiptRepository.findById(id);

        if (receivedReceipt.isPresent()) {
            return receivedReceipt.get();
        } else {
            throw new AppException(4041);
        }
    }

    public ReceivedReceipt updateStatus(Long id, ReceiptStatusRequest status, boolean isFromCustomer) {
        Optional<ReceivedReceipt> receivedReceipt = receivedReceiptRepository.findById(id);

        if (receivedReceipt.isPresent()) {
            ReceivedReceipt receipt = receivedReceipt.get();
            if (isFromCustomer) {
                if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.CANCEL
                        && (receipt.getStatus() != ReceiptStatus.WAITING || receipt.isFlagError())) {
                    throw new AppException(4303);
                }
            }
            if (receipt.getStatus() == ReceiptStatus.DONE) {
                throw new AppException(4303);
            }

            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.CANCEL) {
                if (receipt.getStatus() != ReceiptStatus.WAITING) {
                    throw new AppException(4303);
                }
                receipt.setFlagError(false);
                if (status.getCancelNote() != null && !status.getCancelNote().isEmpty()) {
                    receipt.setCancelNote(status.getCancelNote());
                }
            }
            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.PACKING && receipt.getStatus() != ReceiptStatus.PACKING) {
                generateDeliveryReceipt(receipt);
                receipt.setStatus(ReceiptStatus.PACKING);
            }
            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.WAITING_RANDOM_CHECK) {
                for (ItemReceived itemReceived : receipt.getItemReceivedList()) {
                    itemReceived.setRandomNumberCheck(itemReceived.getNumberReceived());
                }
                if (receipt.getTruck() == null || receipt.getDriver() == null) {
                    throw new AppException(4401);
                }
                receipt.setStatus(ReceiptStatus.WAITING_RANDOM_CHECK);
            }
            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.WASHING) {
                for (ItemReceived itemReceived : receipt.getItemReceivedList()) {
                    itemReceived.setNumberAfterProduction(itemReceived.getRandomNumberCheck());
                }
                receipt.setStatus(ReceiptStatus.WASHING);
            }
            if (ReceiptStatus.valueOf(status.getStatus()) == ReceiptStatus.STAFF_RECEIVED) {
                if (receipt.getReceiptImage() == null || receipt.getReceiptImage().isEmpty()) {
                    throw new AppException(4309);
                }
                receipt.setNumberOfCheckBags(receipt.getBagNumber());
                receipt.setFlagError(false);
                receipt.setStatus(ReceiptStatus.STAFF_RECEIVED);
            }
            receipt.setStatus(ReceiptStatus.valueOf(status.getStatus()));
            addReceiptHistory(receipt, receipt.getStatus());
            return receivedReceiptRepository.save(receipt);
        } else {throw new AppException(4041);}
    }
    void generateDeliveryReceipt(ReceivedReceipt receivedReceipt){
//        String[] weightArr = receivedReceipt.getWeight().split(",");
//        float totalW = 0.0f;
//        for(String w: weightArr){
//            totalW = totalW + Float.parseFloat(w);
//        }

        List<ItemReceived> itemReceivedList = receivedReceipt.getItemReceivedList();
        List<ItemDeliveryCreateRequest> itemDeliveryCreateRequests = new ArrayList<>();
        for(ItemReceived item: itemReceivedList){
            ItemDeliveryCreateRequest request = ItemDeliveryCreateRequest.builder()
                    .productItemId(item.getProductItem().getId())
                    .receivedReceiptId(receivedReceipt.getId())
                    .numberReceived(item.getNumberReceived())
                    .numberDelivery(item.getNumberAfterProduction())
                    .numberAfterProduction(item.getNumberAfterProduction())
                    .laundryFormValue(item.getLaundryForm() != null ? item.getLaundryForm().getValue() : null)
                    .note(item.getNote())
                    .randomNumberCheck(item.getRandomNumberCheck()).build();
            itemDeliveryCreateRequests.add(request);
        }
//        List<SpecialInstructionOfReceipt> specialInstructionOfReceipts = receivedReceipt.getSpecialInstructionOfReceipts();
//        List<String> specialInstruction = new ArrayList<>();
//        for(SpecialInstructionOfReceipt item: specialInstructionOfReceipts){
//            specialInstruction.add(item.getSpecialInstruction().getValue());
//        }


        DeliveryReceiptCreateRequest request = DeliveryReceiptCreateRequest.builder()
                .customerId(receivedReceipt.getCustomer().getId())
                .productTypeValue(receivedReceipt.getProductType().getValue())
                .numberRoom(receivedReceipt.getNumberRoom())
                .deliveryDate(receivedReceipt.getDeliveryDate())
                .bagNumber(0)
                .numberOfLooseBags(receivedReceipt.getNumberOfLooseBags())
                .weight(0f)
                .note(receivedReceipt.getNote())
                .receivedIds(Collections.singletonList(receivedReceipt.getId()))
                .itemDeliveryCreateRequests(itemDeliveryCreateRequests)
                .isExpress(receivedReceipt.isExpress())
                .isAutoGen(true)
                .build();

        deliveryReceiptService.create(request, true, false);
    }

    public ReceivedReceipt findById(Long id) {
        Optional<ReceivedReceipt> optional = receivedReceiptRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new AppException(4041);
        }
    }

    public DeliveryReceipt findDataById(Long id) {
        Optional<DeliveryReceipt> optional = deliveryReceiptRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new AppException(4041);
        }
    }

    public ReceivedReceipt confirmNoError(Long id, FsConfirmRequest request) {
        Optional<ReceivedReceipt> receivedReceipt = receivedReceiptRepository.findById(id);

        if (receivedReceipt.isPresent()) {
            ReceivedReceipt receipt = receivedReceipt.get();
            receipt.setFsNote(request.getFsNote());
            receipt.setFlagError(false);
            addReceiptHistory(receipt, ReceiptStatus.ACCEPT);
            return receivedReceiptRepository.save(receipt);
        } else {
            throw new AppException(4041);
        }
    }

    public ReceivedReceipt updateImages(Long id, ArrayList<String> images, CreatedSourceType createdSourceType) {
        Optional<ReceivedReceipt> receivedReceipt = receivedReceiptRepository.findById(id);

        if (receivedReceipt.isPresent()) {
            ReceivedReceipt receipt = receivedReceipt.get();
            List<ImageReceipt> imageArr = receipt.getImageReceipts();
            for(String img: images){
                if(!checkImageExist(img, imageArr, createdSourceType)){
                    ImageReceipt imageReceipt = ImageReceipt.builder()
                            .receivedReceipt(receipt)
                            .image(img)
                            .createdSourceType(createdSourceType)
                            .build();
                    imageArr.add(imageReceipt);
                }
            }

            receipt.setImageReceipts(imageArr);
            return receivedReceiptRepository.save(receipt);
        } else {
            throw new AppException(4041);
        }
    }

    public ReceivedReceipt deleteImages(Long id, List<ImageReceipt> imageReceiptRemoveList) {
        ReceivedReceipt receivedReceipt = receivedReceiptRepository.findById(id).orElseThrow(() -> new AppException(4041));
        List<ImageReceipt> imageReceiptList = receivedReceipt.getImageReceipts();

        for (ImageReceipt imageReceiptRemoved: imageReceiptRemoveList) {
            imageReceiptList.remove(imageReceiptRemoved);
            imageReceiptRemoved.setReceivedReceipt(null);
        }
        return receivedReceiptRepository.save(receivedReceipt);
    }

    boolean checkImageExist(String imageName, List<ImageReceipt> imageArr, CreatedSourceType createdSourceType){
        for(ImageReceipt image: imageArr){
            if(image.getImage().equalsIgnoreCase(imageName)
                    && image.getCreatedSourceType().equals(createdSourceType)){
                return true;
            }
        }
        return false;
    }

    public ReceivedReceipt requestReCheck(Long id) {
        Optional<ReceivedReceipt> receivedReceipt = receivedReceiptRepository.findById(id);

        if (receivedReceipt.isPresent()) {
            ReceivedReceipt receipt = receivedReceipt.get();
            receipt.setFlagError(true);
            receipt.setMarkError(true);
            addReceiptHistory(receipt, ReceiptStatus.REQUEST_RECHECK);
            return receivedReceiptRepository.save(receipt);
        } else {
            throw new AppException(4041);
        }
    }

    public ReceivedReceipt debtClosing(Long id){
        Optional<ReceivedReceipt> receivedReceipt = receivedReceiptRepository.findById(id);

        if (receivedReceipt.isPresent()) {
            ReceivedReceipt receipt = receivedReceipt.get();

            if(receipt.getStatus() == ReceiptStatus.DONE) {
                throw new AppException(4304);
            }
            Integer numberReceipt = receivedReceiptRepository.findNumberDeliveryReceiptHasNotDone(id);
            if(numberReceipt > 0){
                throw new AppException(4301);
            }
            receipt.setStatus(ReceiptStatus.DONE);
            receipt.setDebtClosing(true);
            addReceiptHistory(receipt, ReceiptStatus.DEBT_CLOSING);
            return receivedReceiptRepository.save(receipt);
        } else {
            throw new AppException(4041);
        }
    }

    public List<BriefDataDto> findCodeLike(String code, String status, Pageable pageable){
        return receivedReceiptRepository.findCodeLike(code, status, pageable);
    }

    public List<BriefDataDto> getDeliveryCode(Long receiptId){
        return getDeliveryCodeDAO.getDeliveryCode(receiptId);
    }

    public void addReceiptHistory(ReceivedReceipt receivedReceipt, ReceiptStatus status) {
        ReceivedReceiptHistory receiptHistory = ReceivedReceiptHistory
                .builder()
                .receivedId(receivedReceipt.getId())
                .code(receivedReceipt.getCode())
                .status(status)
                .build();
        receiptHistoryRepository.save(receiptHistory);
    }

    public List<BriefDataDto> findAllByForDebt(Long customerId, Instant fromDate, Instant toDate, String productTypeValue){
        Long productTypeId = productTypeService.getByValue(productTypeValue).getId();
        return receivedReceiptRepository.findAllByForDebt(customerId, DateTimeUtil.convertToUTC(fromDate, ebstUserRequest),
                DateTimeUtil.convertToUTC(toDate, ebstUserRequest), productTypeId);
    }

    public void isFlagError(ReceivedReceipt receivedReceipt, ReceivedReceiptUpdateRequest request) {
        boolean errorUpdate = false;
        boolean isBreak = false;
        if (receivedReceipt.isFlagError() && !receivedReceipt.getStatus().equals(ReceiptStatus.WAITING)) {
            for (ItemReceived itemReceived : receivedReceipt.getItemReceivedList()) {
                if (isBreak) {break;}
                for (ItemReceivedCreateRequest itemReceivedRequest : request.getItemReceivedCreateRequests()) {
                    if (receivedReceipt.getItemReceivedList().size() != request.getItemReceivedCreateRequests().size() ||
                            itemReceivedRequest.getId() == null) {
                        setErrorReceipt(receivedReceipt);
                        isBreak = true;
                        break;
                    }
                    if (Objects.equals(itemReceived.getId(), itemReceivedRequest.getId())) {
                        if (request.getBagNumber() != null && request.getBagNumber() == receivedReceipt.getBagNumber() &&
                                itemReceivedRequest.getNumberReceived() == itemReceived.getNumberReceived()) {
                            errorUpdate = true;
                        } else {
                            errorUpdate = false;
                            isBreak = true;
                            setErrorReceipt(receivedReceipt);
                            break;
                        }
                    }
                }
            }
            if (errorUpdate) {
                throw new AppException(4306);
            }
        }
    }

    public void setErrorReceipt (ReceivedReceipt receivedReceipt) {
        receivedReceipt.setStatus(ReceiptStatus.WAITING);
        receivedReceipt.setSignatureStaff("{}");
        receivedReceipt.setSignatureCustomer("{}");
        receivedReceipt.setReceiptImage(null);
    }

}
