package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.Department;

import java.util.List;
import java.util.Optional;

public interface IDepartmentRepository extends BaseJpaRepository<Department,Long>, JpaSpecificationExecutor<Department> {
    Optional<Department> findByCodeOrName(String code, String name);

    List<Department> findByNameAndIdNot(String name, long id);


}