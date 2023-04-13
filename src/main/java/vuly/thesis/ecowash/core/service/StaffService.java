package vuly.thesis.ecowash.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.FieldStaffLinkCustomer;
import vuly.thesis.ecowash.core.entity.Staff;
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.mapper.CustomerAccountMapper;
import vuly.thesis.ecowash.core.payload.dto.StaffDto;
import vuly.thesis.ecowash.core.payload.request.StaffCreateRequest;
import vuly.thesis.ecowash.core.payload.request.StaffSearchRequest;
import vuly.thesis.ecowash.core.payload.request.StaffUpdatePasswordRequest;
import vuly.thesis.ecowash.core.payload.request.StaffUpdateRequest;
import vuly.thesis.ecowash.core.repository.CustomerRepository;
import vuly.thesis.ecowash.core.repository.StaffRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.CustomerAccountDAO;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.StaffDtoDAO;
import vuly.thesis.ecowash.core.validation.StaffValidation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class StaffService {
	@Autowired
	private StaffRepository staffRepository;
	@Autowired
	private StaffValidation staffValidation;

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	StaffDtoDAO staffDtoDAO;
	@Autowired
	CustomerAccountDAO customerAccountDAO;
	@Autowired
	CustomerAccountMapper customerAccountMapper;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	UserService userService;

	public Staff create(StaffCreateRequest request) {
		String validationResult = staffValidation.checkExistedStaff(request.getEmail(), request.getCode(), request.getUsername());
		if (validationResult != null) {
			List<Object> params = new ArrayList();
			params.add(validationResult);
			throw new AppException(HttpStatus.BAD_REQUEST, 4018, params);
		}

		Staff newStaff = createNewStaffFromRequest(request);
		List<FieldStaffLinkCustomer> fieldStaffLinkCustomerList = new ArrayList<>();
		if (request.getRoles().contains("FIELD_STAFF")) {
			for (long id : request.getCustomerId()) {
				FieldStaffLinkCustomer fieldStaffLinkCustomerBuilder = FieldStaffLinkCustomer.builder()
						.fieldStaff(newStaff)
						.customer(customerRepository.findByIdAndActiveIsTrue(id))
						.build();
				fieldStaffLinkCustomerList.add(fieldStaffLinkCustomerBuilder);
			}
		}
		newStaff.getFieldStaffLinkCustomers().addAll(fieldStaffLinkCustomerList);
		Staff staff = staffRepository.save(newStaff);
		return staffRepository.save(staff);
	}


	public Staff createNewStaffFromRequest(StaffCreateRequest request){
		Staff.StaffBuilder staffBuilder = Staff
				.builder()
				.fullName(request.getFullName())
				.code(request.getCode())
				.username(request.getUsername())
				.email(request.getEmail())
				.phoneNumber(request.getPhoneNumber())
				.role(request.getRoles())
				.note(request.getNote())
				.status(Status.ACTIVE)
				.fieldStaffLinkCustomers(new ArrayList<>());
		if(request.getDepartmentId() > 0){
			staffValidation.validDepartment(request.getDepartmentId());
			staffBuilder.department(departmentService.getDepartmentById(request.getDepartmentId()));
		}

		if(request.getIsCustomer() != null && request.getRoles().contains("ROLE_CUSTOMER")){
			staffBuilder.isCustomer(request.getIsCustomer());
			staffValidation.validCustomer(request.getCustomerId().get(0));
			staffBuilder.customerId(request.getCustomerId().get(0));
		} else {
			staffBuilder.isCustomer(false);
		}
		return staffBuilder.build();
	}

	public Staff update(Long staffId, StaffUpdateRequest request) {
		Optional<Staff> staff = staffRepository.findById(staffId);

		if (staff.isPresent()) {
			Staff updateStaff = staff.get();
			updateStaff.setFullName(request.getFullName());
			updateStaff.setPhoneNumber(request.getPhoneNumber());
			updateStaff.setEmail(request.getEmail());
			updateStaff.setNote(request.getNote());
			updateStaff.setRole(request.getRoles());
			if(request.getDepartmentId() > 0){
				staffValidation.validDepartment(request.getDepartmentId());
				updateStaff.setDepartment(departmentService.getDepartmentById(request.getDepartmentId()));
			} else {
				updateStaff.setDepartment(null);
			}

			List<FieldStaffLinkCustomer> fieldStaffLinkCustomerList = new ArrayList<>();
			if (updateStaff.getRole().contains("FIELD_STAFF")) {
				for (long id : request.getCustomerId()) {
					FieldStaffLinkCustomer fieldStaffLinkCustomerBuilder = FieldStaffLinkCustomer.builder()
							.fieldStaff(updateStaff)
							.customer(customerRepository.findByIdAndActiveIsTrue(id))
							.build();
					fieldStaffLinkCustomerList.add(fieldStaffLinkCustomerBuilder);
				}
			}
			updateStaff.getFieldStaffLinkCustomers().clear();
			updateStaff.getFieldStaffLinkCustomers().addAll(fieldStaffLinkCustomerList);
			return staffRepository.save(updateStaff);
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Staff" + staffId}));
		}
	}


	public Staff updateStatus(Long staffId, Status status) {
		Optional<Staff> optStaff = staffRepository.findById(staffId);
		if (optStaff.isPresent()) {
			Staff staff = optStaff.get();
			if (status == Status.ACTIVE) {
				staff.setActivatedTime(Instant.now());
			}
			if (status == Status.DEACTIVE) {
			}
			staff.setStatus(status);
			return staffRepository.save(staff);
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Staff" + staffId}));
		}
	}

	public Staff getStaff(Long staffId) {
		Optional<Staff> staffOptional = staffRepository.findById(staffId);
		if (staffOptional.isPresent()) {
			Staff staff = staffOptional.get();
			return staff;
		} else {
			throw new AppException(4041, new ArrayList<>().add(new String[]{"Staff" + staffId}));
		}
	}

	@Transactional(readOnly = true)
	public StaffDto getCustomerAccount(long id) {
		Staff customerAccount = getStaff(id);
		return customerAccountMapper.entityToDto(customerAccount);
	}



	public Page<StaffDto> getStaffList(StaffSearchRequest request, Pageable pageable) {
		return staffDtoDAO.findAll(request, pageable);
	}


	public Page<StaffDto> getCustomerAccountList(StaffSearchRequest request, Pageable pageable) {
		return customerAccountDAO.findAll(request, pageable);
	}
}
