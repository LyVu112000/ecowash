package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.Role;

import java.util.Optional;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {


	Optional<Role> findByName(String name);

	@Override
	void delete(Role role);
}
