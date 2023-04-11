package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.Customer;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.CustomerDto;
import vuly.thesis.ecowash.core.payload.request.CustomerCreateRequest;
import vuly.thesis.ecowash.core.payload.request.CustomerSearchRequest;
import vuly.thesis.ecowash.core.payload.request.CustomerUpdateRequest;
import vuly.thesis.ecowash.core.repository.CustomerRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.CustomerDtoDAO;
import vuly.thesis.ecowash.core.validation.CustomerValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class CustomerService {
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CustomerValidation customerValidation;
	@Autowired
	PieceTypeService pieceTypeService;
	@Autowired
	ProductTypeService productTypeService;
	@Autowired
	PaidByService paidByService;
	@Autowired
	private CustomerDtoDAO customerDtoDAO;

	public Customer create(CustomerCreateRequest request) {
		if (customerValidation.checkExistedCustomer(request.getCode())) {
			List<Object> params = new ArrayList();
			params.add(request.getCode());
			throw new AppException(HttpStatus.BAD_REQUEST, 4018, params);
		}

		Customer customer = createNewCustomerFromRequest(request);
		return customerRepository.save(customer);
	}

	public Customer createNewCustomerFromRequest(CustomerCreateRequest request){
		Customer.CustomerBuilder customerBuilder = Customer
				.builder()
				.code(request.getCode())
				.fullName(request.getFullName())
				.email(request.getEmail())
				.phoneNumber(request.getPhoneNumber())
				.address(request.getAddress())
				.note(request.getNote())
				.logo(request.getLogo())
				.tax(request.getTax())
				.active(true);
		return customerBuilder.build();
	}


	public Customer update(Long customerId, CustomerUpdateRequest request) {
		Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

		if (optionalCustomer.isPresent()) {
			Customer customer = optionalCustomer.get();
//			customer.setCode(request.getCode());
			customer.setFullName(request.getFullName());
			customer.setPhoneNumber(request.getPhoneNumber());
			customer.setAddress(request.getAddress());
			customer.setLogo(request.getLogo());
			customer.setNote(request.getNote());
			customer.setTax(request.getTax());
			customer.setEmail(request.getEmail());
			return customerRepository.save(customer);
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Customer" + customerId}));
		}
	}
	public Customer getCustomer(Long customerId) {
		Optional<Customer> customer = customerRepository.getById(customerId);
		if (customer.isPresent()) {
			return customer.get();
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Customer" + customerId}));
		}
	}
	@Transactional(readOnly = true)
	public Page<CustomerDto> findAllCustomer(CustomerSearchRequest request, Pageable pageable) {
		return customerDtoDAO.findAll(request, pageable);
	}

	public Customer deactive(Long id) {
		Optional<Customer> optional = customerRepository.getById(id);
		if (optional.isPresent()) {
			Customer customer = optional.get();
			customer.setActive(false);
			return customerRepository.save(customer);
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Customer" + id}));
		}
	}
	public Customer active(Long id) {
		Optional<Customer> optional = customerRepository.getById(id);
		if (optional.isPresent()) {
			Customer customer = optional.get();
			customer.setActive(true);
			return customerRepository.save(customer);
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Customer" + id}));
		}
	}

	public Customer findById(long value) {
		Optional<Customer> optional = customerRepository.findById(value);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Customer" + value}));
		}
	}

}
