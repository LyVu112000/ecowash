package vuly.thesis.ecowash.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.Truck;
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.TruckDto;
import vuly.thesis.ecowash.core.payload.request.TruckCreateRequest;
import vuly.thesis.ecowash.core.payload.request.TruckSearchRequest;
import vuly.thesis.ecowash.core.payload.request.TruckUpdateRequest;
import vuly.thesis.ecowash.core.repository.TruckRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.TruckDtoDAO;
import vuly.thesis.ecowash.core.validation.TruckValidation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class TruckService {
	@Autowired
	private TruckRepository truckRepository;
	@Autowired
	private TruckValidation truckValidation;
	@Autowired
	private StaffService staffService;
	@Autowired
	TruckDtoDAO truckDtoDAO;

	public Truck create(TruckCreateRequest request) {
		String validationResult = truckValidation.checkExistedTruck(request.getCode());
		if (validationResult != null) {
			List<Object> params = new ArrayList();
			params.add(validationResult);
			throw new AppException(HttpStatus.BAD_REQUEST, 4018, params);
		}

		Truck newTruck = createNewTruckFromRequest(request);
		Truck truck = truckRepository.save(newTruck);
		return truck;
	}


	public Truck createNewTruckFromRequest(TruckCreateRequest request){
		Truck.TruckBuilder truckBuilder = Truck
				.builder()
				.code(request.getCode())
				.status(Status.ACTIVE);
		if(request.getStaffId() > 0){
			truckValidation.validStaff(request.getStaffId());
			truckBuilder.staff(staffService.getStaff(request.getStaffId()));
		}
		return truckBuilder.build();
	}

	public Truck update(Long truckId, TruckUpdateRequest request) {
		Optional<Truck> truck = truckRepository.findById(truckId);

		if (truck.isPresent()) {
			Truck updateTruck = truck.get();
			if(request.getStaffId() > 0){
				truckValidation.validStaff(request.getStaffId());
				updateTruck.setStaff(staffService.getStaff(request.getStaffId()));
			} else {
				updateTruck.setStaff(null);
			}

			return truckRepository.save(updateTruck);
		} else {
			throw new AppException(4041);
		}
	}

	public Truck updateStatus(Long truckId, Status status) {
		Optional<Truck> optTruck = truckRepository.findById(truckId);

		if (optTruck.isPresent()) {
			Truck truck = optTruck.get();

			if (status == Status.ACTIVE) {
				truck.setActivatedTime(Instant.now());
			}

			truck.setStatus(status);
			return truckRepository.save(truck);
		} else {
			throw new AppException(4041);
		}
	}

	public Truck getTruck(Long truckId) {
		Optional<Truck> truckOptional = truckRepository.findById(truckId);

		if (truckOptional.isPresent()) {
			Truck truck = truckOptional.get();
			return truck;
		} else {
			throw new AppException(4041);
		}
	}

	public Page<TruckDto> getTruckList(TruckSearchRequest request, Pageable pageable) {
		return truckDtoDAO.findAll(request, pageable);
	}

}
