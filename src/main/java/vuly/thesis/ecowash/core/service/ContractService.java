package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.*;
import vuly.thesis.ecowash.core.entity.type.ContractStatus;
import vuly.thesis.ecowash.core.entity.type.PaymentTerm;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.mapper.ContractMapper;
import vuly.thesis.ecowash.core.payload.dto.ContractDto;
import vuly.thesis.ecowash.core.payload.request.*;
import vuly.thesis.ecowash.core.repository.ContractRepository;
import vuly.thesis.ecowash.core.repository.core.IProductItemRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.ContractDtoDAO;
import vuly.thesis.ecowash.core.validation.ContractValidation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class ContractService {
	@Autowired
	ContractRepository contractRepository;
	@Autowired
	PaidByService paidByService;
	@Autowired
	CustomerService customerService;
	@Autowired
	ProductItemService productItemService;
	@Autowired
	ContractValidation contractValidation;
	@Autowired
	ContractDtoDAO contractDtoDAO;
	@Autowired
	ContractMapper contractMapper;
	@Autowired
	ProductTypeService productTypeService;
	@Autowired
	IProductItemRepository productItemRepository;


	public Contract create(ContractCreateRequest request) {
		Contract contract = createNewContract(request);
		return contractRepository.save(contract);
	}

	public Contract createNewContract(ContractCreateRequest request){
		Customer customer = customerService.getCustomer(request.getCustomerId());
		Contract contract = new Contract().toBuilder()
				.tax(request.getTax())
				.note(request.getNote())
				.email(request.getEmail())
				.address(request.getAddress())
				.phoneNumber(request.getPhoneNumber())
				.status(ContractStatus.WAITING)
				.validDate(request.getValidDate())
				.expiredDate(request.getExpiredDate())
				.representative(request.getRepresentative())
				.representativePosition(request.getRepresentativePosition())
				.isExtend(request.getIsExtend())
				.build();

		String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));

		Integer sequenceNumberOpt = contractRepository.findByLikeCodeAndMaxSequenceNumber(String.format("%s-%s", customer.getCode(), dateNow));
		if (sequenceNumberOpt != null) {
			contract.setCode(String.format("%s-%s-%03d", customer.getCode(), dateNow, sequenceNumberOpt + 1));
			contract.setSequenceNumber(sequenceNumberOpt + 1);
		} else {
			contract.setCode(String.format("%s-%s-%03d", customer.getCode(), dateNow, 1));
			contract.setSequenceNumber(1);
		}

		if(request.getPaidByValue() != null && !request.getPaidByValue().isEmpty()) {
			contract.setPaidBy(paidByService.getByValue(request.getPaidByValue()));
		} else {
			throw new AppException(4105);
		}

		if(request.getCustomerId() > 0) {
			contract.setCustomer(customerService.getCustomer(request.getCustomerId()));
		}

		if(request.getPaymentTerm() != null && !request.getPaymentTerm().isEmpty()) {
			contract.setPaymentTerm(PaymentTerm.valueOf(request.getPaymentTerm()));
        } else {
			throw new AppException(4105);
		}

		List<ContractTime> contractTimeList = new ArrayList<>();
		for (ContractTimeCreateRequest contractTimeCreateRequest : request.getContractTimeCreateRequestList()) {
			ContractTime.ContractTimeBuilder contractTimeBuilder = ContractTime
					.builder()
					.contract(contract)
					.timeDelivery(contractTimeCreateRequest.getTimeDelivery())
					.timeReceived(contractTimeCreateRequest.getTimeReceived())
					.note(contractTimeCreateRequest.getNote());

			if(contractTimeCreateRequest.getProductTypeValue() != null && !contractTimeCreateRequest.getProductTypeValue().isEmpty()) {
				contractTimeBuilder.productType(productTypeService.getByValue(contractTimeCreateRequest.getProductTypeValue()));
			} else {
				throw new AppException(4104);
			}
			contractTimeList.add(contractTimeBuilder.build());
		}
		contract.setContractTimes(contractTimeList);

		if ((request.getContractProductCreateRequestList().size() < 1)) {
			throw new AppException(4107);
		}
		List<ContractProduct> contractProductList = new ArrayList<>();
		for (ContractProductCreateRequest contractProductCreateRequest : request.getContractProductCreateRequestList()) {
			ContractProduct.ContractProductBuilder contractProductBuilder = ContractProduct
					.builder()
					.contract(contract)
					.note(contractProductCreateRequest.getNote())
					.isCommonProduct(contractProductCreateRequest.isCommonProduct());

			if (contractProductCreateRequest.getProductItemId() > 0) {
				contractProductBuilder.productItem(productItemService.getProductItemById(contractProductCreateRequest.getProductItemId()));
			}
			contractProductList.add(contractProductBuilder.build());
		}

		for (long id : productItemRepository.findByIsOther(true ).stream().map(ProductItem::getId).toList()) {
			ContractProduct.ContractProductBuilder contractProductBuilder = ContractProduct
					.builder()
					.contract(contract)
					.isCommonProduct(false)
					.productItem(productItemService.getProductItemById(id));
			contractProductList.add(contractProductBuilder.build());
		}
		contract.setContractProducts(contractProductList);
		//deactive HĐ cũ -> gia hạn tạo HĐ mới
		if(request.getIsExtend() && request.getContractId() != 0){
			Contract contract1 = findById(request.getContractId());
			contract1.setActive(false);
			contractRepository.save(contract1);
		}
		return contractRepository.save(contract);
	}

	public Contract update(Long id, ContractUpdateRequest request) {
		//update contract
		Contract contract = findById(id);
		if (contract.getStatus() != ContractStatus.APPROVED || contract.getStatus() != ContractStatus.CANCEL) {
			contract.setValidDate(request.getValidDate());
			contract.setExpiredDate(request.getExpiredDate());
		}
		contract.setTax(request.getTax());
		contract.setNote(request.getNote());
		contract.setEmail(request.getEmail());
		contract.setAddress(request.getAddress());
		contract.setPaymentTerm(request.getPaymentTerm());
		contract.setPhoneNumber(request.getPhoneNumber());
		contract.setRepresentative(request.getRepresentative());
		contract.setRepresentativePosition(request.getRepresentativePosition());

		if (request.getPaidByValue() != null) {
			contract.setPaidBy(paidByService.getByValue(request.getPaidByValue()));
		}

		//update ContractTime
		List<ContractTime> newContractTimeList = new ArrayList<>();
		for (ContractTimeUpdateRequest contractTimeUpdateRequest : request.getContractTimeUpdateRequestList()) {
			boolean isProductTimeCreate = true;
			for (ContractTime contractTime : contract.getContractTimes()) {
				if (contractTime.getId().equals(contractTimeUpdateRequest.getId())) {
					isProductTimeCreate = false;
					contractTime.setTimeReceived(contractTimeUpdateRequest.getTimeReceived());
					contractTime.setTimeReceived(contractTimeUpdateRequest.getTimeReceived());
					contractTime.setNote(contractTimeUpdateRequest.getNote());
					if(contractTimeUpdateRequest.getProductTypeValue() != null) {
						contractTime.setProductType(productTypeService.getByValue(contractTimeUpdateRequest.getProductTypeValue()));
					}
					newContractTimeList.add(contractTime);
					break;
				}
				break;
			}
			if (isProductTimeCreate) {
				ContractTime.ContractTimeBuilder contractTimeBuilder = ContractTime
						.builder()
						.contract(contract)
						.timeDelivery(contractTimeUpdateRequest.getTimeDelivery())
						.timeReceived(contractTimeUpdateRequest.getTimeReceived())
						.note(contractTimeUpdateRequest.getNote());
				if(contractTimeUpdateRequest.getProductTypeValue() != null) {
					contractTimeBuilder.productType(productTypeService.getByValue(contractTimeUpdateRequest.getProductTypeValue()));
				}
				newContractTimeList.add(contractTimeBuilder.build());
			}
		}
		contract.getContractTimes().clear();
		contract.getContractTimes().addAll(newContractTimeList);

		//update contractProduct
		if ((request.getContractProductUpdateRequestList().size() < 1)) {
			throw new AppException(4107);
		}
		List<ContractProduct> newContracProductList = new ArrayList<>();
		for (ContractProductUpdateRequest contractProductRequest : request.getContractProductUpdateRequestList()) {
			boolean isCreate = true;
			for (ContractProduct contractProduct : contract.getContractProducts()) {
				if (contractProduct.getId().equals(contractProductRequest.getId())) {
					isCreate = false;
					contractProduct.setNote(contractProductRequest.getNote());
					contractProduct.setCommonProduct(contractProductRequest.isCommonProduct());
					if (contractProductRequest.getProductItemId() > 0) {
						contractProduct.setProductItem(productItemService.getProductItemById(contractProductRequest.getProductItemId()));
					}
					newContracProductList.add(contractProduct);
					break;
				}
			}
			if (isCreate) {
				ContractProduct.ContractProductBuilder contractProductBuilder = ContractProduct
						.builder()
						.contract(contract)
						.note(contractProductRequest.getNote())
						.isCommonProduct(contractProductRequest.isCommonProduct());

				if (contractProductRequest.getProductItemId() > 0) {
					contractProductBuilder.productItem(productItemService.getProductItemById(contractProductRequest.getProductItemId()));
				}
				newContracProductList.add(contractProductBuilder.build());
			}
		}
		contract.getContractProducts().clear();
		contract.getContractProducts().addAll(newContracProductList);
		return contractRepository.save(contract);
	}

	public Contract findById(long id) {
		Optional<Contract> optional = contractRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"contract" + id}));
		}
	}

	@Transactional(readOnly = true)
	public Page<ContractDto> findAllContract(ContractSearchRequest request, Pageable pageable) {
		return contractDtoDAO.findAll(request, pageable);
	}
	@Transactional(readOnly = true)
	public ContractDto findContractById(long id) {
		Contract contract = findById(id);
		return contractMapper.entityToDto(contract);
	}

//	@Transactional(readOnly = true)
//	public Long findContractByCustomerId(long customerId) {
//		return contractRepository.findContractIdByCustomerId(customerId);
//	}

	public Contract approvedStatus(Long id) {
		Optional<Contract> optional = contractRepository.findById(id);
		if (optional.isPresent()) {
			Contract contract = optional.get();
			if (contract.getStatus().equals(ContractStatus.APPROVED)
					|| contract.getStatus().equals(ContractStatus.CANCEL)) {
				throw new AppException(4106);
			}
			contractValidation.checkExistedCustomer(contract.getCustomer().getId());
			contract.setStatus(ContractStatus.APPROVED);
			return contractRepository.save(contract);
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Contract" + id}));
		}
	}

	public Contract cancelStatus(Long id) {
		Optional<Contract> optional = contractRepository.findById(id);
		if (optional.isPresent()) {
			Contract contract = optional.get();
			if (contract.getStatus().equals(ContractStatus.CANCEL)) {
				throw new AppException(4106);
			}
			contract.setStatus(ContractStatus.CANCEL);
			return contractRepository.save(contract);
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Contract" + id}));
		}
	}

	public ContractDto getContractBriefList(Long customerId) {
		Optional<Contract> contractOpt = contractRepository.findByCustomerIdAndApprovedStatus(customerId);
		ContractDto contractBrief = new ContractDto();
		if (contractOpt.isPresent()) {
			Contract contract = contractOpt.get();
			contractBrief.setCode(contract.getCode());
			contractBrief.setId(contract.getId());
			contractBrief.setStatus(contract.getStatus().toString());
			contractBrief.setCustomerId(contract.getCustomer().getId());
			contractBrief.setCustomerName(contract.getCustomer().getFullName());
		}
		return contractBrief;
	}
}
