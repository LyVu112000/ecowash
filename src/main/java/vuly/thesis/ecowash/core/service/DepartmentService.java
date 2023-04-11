package vuly.thesis.ecowash.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.Department;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.dto.DepartmentDto;
import vuly.thesis.ecowash.core.payload.request.DepartmentCreateRequest;
import vuly.thesis.ecowash.core.payload.request.DepartmentSearchRequest;
import vuly.thesis.ecowash.core.payload.request.DepartmentUpdateRequest;
import vuly.thesis.ecowash.core.repository.core.IDepartmentRepository;
import vuly.thesis.ecowash.core.repository.jdbc.DAO.DepartmentDtoDAO;
import vuly.thesis.ecowash.core.validation.DepartmentValidation;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentValidation departmentValidation;
    @Autowired
    private final DepartmentDtoDAO departmentDtoDAO;
    @Autowired
    private final IDepartmentRepository departmentRepository;

    public DepartmentService(DepartmentDtoDAO departmentDtoDAO,
                             IDepartmentRepository IDepartmentRepository) {
        this.departmentDtoDAO = departmentDtoDAO;
        this.departmentRepository = IDepartmentRepository;
    }
    @Transactional(readOnly = true)
    public Page<DepartmentDto> findAllDepartment(DepartmentSearchRequest request, Pageable pageable) {
        return departmentDtoDAO.findAll(request, pageable);
    }

    public Department create(DepartmentCreateRequest request) {
        departmentValidation.createCheck( request);
        Department department = mapping(request);
        return departmentRepository.save(department);
    }

    public Department mapping(DepartmentCreateRequest request) {
        Department department = new Department();
        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setNote(request.getNote());
        department.setPhoneNumber(request.getPhoneNumber());
        return department;
    }

    public Department update(Long id, DepartmentUpdateRequest request) {
        departmentValidation.updateCheck(id, request);
        Optional<Department> optional = departmentRepository.findById(id);
        if (!optional.isPresent()) {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"Department" + id}));
        }
        Department department = optional.get();
        department.setName(request.getName());
        department.setNote(request.getNote());
        department.setPhoneNumber(request.getPhoneNumber());
        return departmentRepository.save(department);
    }

    public Department deactive(Long id) {
        departmentValidation.checkBeforeDeactive(id);
        Optional<Department> optional = departmentRepository.findById(id);
        if (optional.isPresent()) {
            Department department = optional.get();
            department.setActive(false);
            return departmentRepository.save(department);
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"Department" + id}));
        }
    }
    public Department active(Long id) {
        Optional<Department> optional = departmentRepository.findById(id);
        if (optional.isPresent()) {
            Department department = optional.get();
            department.setActive(true);
            return departmentRepository.save(department);
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"Department" + id}));
        }
    }


    public Department getDepartmentById(Long id) {
        Optional<Department> optional = departmentRepository.findById(id);
        if (optional.isPresent()) {
            Department department = optional.get();
            return department;
        } else {
            throw new AppException(4041, new ArrayList<>().add(new String[]{"Department" + id}));
        }
    }
}
