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

	String LOGOUT_USER = "UPDATE user u SET u.account_expired = 1 WHERE u.username = :username AND u.tenant_id = :tenant_id";
	String DELETE_USER = "UPDATE user u SET u.account_locked = 1, u.account_expired = 1 WHERE u.id = :id";
	String UPDATE_USER = "UPDATE user SET " +
			"phone_number = IFNULL(:#{#user.phoneNumber}, phone_number), " +
			"email = IFNULL(:#{#user.email}, email), " +
			"full_name = IFNULL(:#{#user.fullName}, full_name), " +
			"position = IFNULL(:#{#user.position}, position) " +
			"WHERE username = :username";
	String UPDATE_OTHER_USER = "UPDATE user SET " +
			"phone_number = IFNULL(:#{#user.phoneNumber}, phone_number), " +
			"email = IFNULL(:#{#user.email}, email), " +
			"full_name = IFNULL(:#{#user.fullName}, full_name), " +
			"position = IFNULL(:#{#user.position}, position) " +
			"WHERE id = :id";
	String LIST_USERS = "SELECT * FROM user WHERE parent_id = :id";
	String LIST_ALL_USERS = "SELECT * FROM user";
	String REMOVE_ROLES = "DELETE FROM user_role WHERE user_id = :#{#id} AND " +
			"role_id IN :#{#request.roles}";
    String REMOVE_ROLES_BY_USERNAME = "DELETE FROM user_role WHERE user_id = ( SELECT id FROM user WHERE username LIKE :#{#username} ) AND " +
            "role_id IN ( SELECT id FROM role WHERE name IN :#{#request.roles} ) ";
	String REMOVE_ROLE_GROUPS = "DELETE FROM user_role_group WHERE " +
			"user_id = ( SELECT id FROM user WHERE username LIKE :#{#username} ) AND " +
			"role_group_id IN ( SELECT id FROM role WHERE name IN :#{#request.groups} ) ";
	String ADD_ROLE_BY_USERNAME = "INSERT INTO user_role (SELECT T.id as user_id, C.id as role_id FROM  (SELECT id FROM user WHERE username LIKE :#{#username} )" +
			" as T JOIN (SELECT id FROM role WHERE name IN :#{#request.roles} ) as C)";

	String LOCK_ACCOUNT 	= "UPDATE user SET account_locked = 1 WHERE username LIKE :#{#username}";
	String UNLOCK_ACCOUNT 	= "UPDATE user SET account_locked = 0 WHERE username LIKE :#{#username}";
	String SET_HOME_PATH_ID = "UPDATE user SET home_path_id = IFNULL(:homePathId, home_path_id) " +
			"WHERE id = :id";
	String GET_USER_BY_ROLE_AND_ORG = "SELECT u.* FROM user u INNER JOIN user_role ur ON u.id = ur.user_id WHERE ur.role_id = :#{#role_id} AND u.tenant_id = :#{#tenant_id}";
	String DELETE_USER_BY_USER_ID = "UPDATE user u SET u.account_locked = 1, u.account_expired = 1 WHERE u.id = :userId";
	String RESTORE_USER_BY_USER_ID = "UPDATE user u SET u.account_locked = 0, u.account_expired = 0 WHERE u.id = :userId";

	Optional<User> findByUsername(String username);

	Optional<User> findByUsernameAndTenantIdAndIsCustomer(String username, Long tenantId, Boolean isCustomer);

	Optional<User> findById(Long id);

	Boolean existsByUsername(String username);
	User findByEmail(String email);

	@Modifying
	@Query(value = LOGOUT_USER, nativeQuery = true)
	void logoutUser(@Param("username") String username);

	@Modifying
	@Query(value = DELETE_USER, nativeQuery = true)
	void deleteUser(@Param("id") long id);

	@Modifying
	@Query(value = LIST_USERS, nativeQuery = true)
	List<User> listChildUsers(@Param("id") Long id);

	@Modifying
	@Query(value = GET_USER_BY_ROLE_AND_ORG, nativeQuery = true)
	List<User> listUserByRoleAndOrg(@Param("role_id") Long roleId, @Param("tenant_id") Long tenantId);


	@Modifying
	@Query(value = LIST_ALL_USERS, nativeQuery = true)
	List<User> list();


	@Modifying
	@Query(value = LOCK_ACCOUNT, nativeQuery = true)
	void lockAccountByUsername(String username);

	@Modifying
	@Query(value = UNLOCK_ACCOUNT, nativeQuery = true)
	void unlockAccountByUsername(String username);

	@Modifying
	@Query(value = SET_HOME_PATH_ID, nativeQuery = true)
	void setHomePathId(Long id, String homePathId);

	List<User> findByEmailAndAccountEnabled(String email, boolean accountEnabled);
	

	@Modifying
	@Query(value = DELETE_USER_BY_USER_ID, nativeQuery = true)
	void deleteUserByUserId(long userId);

	@Modifying
	@Query(value = RESTORE_USER_BY_USER_ID, nativeQuery = true)
	void restoreUserByUserId(long userId);

	Optional<User> findByIdAndAccountLocked(Long userId, boolean accountLocked);

	@Query(value = "SELECT u.* FROM user u  INNER JOIN user_role ur ON u.id = ur.user_id WHERE ur.role_id = :roleId " +
			" AND u.tenant_id = :tenantId", nativeQuery = true)
	List<User> findByRoleIdAndTenantId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);

	User findByIdAndTenantId(Long id, Long tenantId);
}
