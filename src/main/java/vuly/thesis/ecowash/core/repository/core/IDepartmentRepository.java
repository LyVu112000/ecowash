package vuly.thesis.ecowash.core.repository.core;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vuly.thesis.ecowash.core.entity.Department;

import java.util.List;
import java.util.Optional;

public interface IDepartmentRepository extends BaseJpaRepository<Department,Long>, JpaSpecificationExecutor<Department> {
    Optional<Department> findByCodeOrNameAndTenantId(String code, String name, long tenantId);

    List<Department> findByNameAndTenantIdAndIdNot(String name, long tenantId, long id);


}