package vuly.thesis.ecowash.core.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.entity.Department;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.request.DepartmentCreateRequest;
import vuly.thesis.ecowash.core.payload.request.DepartmentUpdateRequest;
import vuly.thesis.ecowash.core.repository.core.IDepartmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DepartmentValidation {

    private final IDepartmentRepository departmentRepository;


    public void createCheck(DepartmentCreateRequest request) {
        Optional<Department> optional = departmentRepository.findByCodeOrName(request.getCode(), request.getName());
        if (optional.isPresent()) {
            Department department = optional.get();
            List<Object> params = new ArrayList<>();
            if(department.getCode().equals(request.getCode())) {
                params.add(department.getCode());
            }
            if(department.getName().equals(request.getName())) {
                params.add(department.getName());
            }
            throw new AppException(HttpStatus.BAD_REQUEST, 4018, params);
        }
    }

    public void updateCheck(Long id, DepartmentUpdateRequest request) {
        List<Department> departmentList = departmentRepository.findByNameAndIdNot(request.getName(), id);
        for (Department department: departmentList) {
            List<Object> params = new ArrayList<>();
            if(department.getName().equals(request.getName())) {
                params.add(department.getName());
            }
            throw new AppException(HttpStatus.BAD_REQUEST, 4018, params);
        }
    }

    public void checkBeforeDeactive(Long id) {
        Department department = departmentRepository.findById(id).orElseThrow(() -> new AppException(4041));
        // check existed staff
    }

}
