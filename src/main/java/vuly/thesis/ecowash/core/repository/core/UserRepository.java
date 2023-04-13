package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

	String LOGOUT_USER = "UPDATE user u SET u.account_expired = 1 WHERE u.username = :username ";
	String LIST_ALL_USERS = "SELECT * FROM user";

	Optional<User> findByUsername(String username);
	Optional<User> findById(Long id);

	Boolean existsByUsername(String username);

	@Modifying
	@Query(value = LOGOUT_USER, nativeQuery = true)
	void logoutUser(@Param("username") String username);

	@Modifying
	@Query(value = LIST_ALL_USERS, nativeQuery = true)
	List<User> list();

}
