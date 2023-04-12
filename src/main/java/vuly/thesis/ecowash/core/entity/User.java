package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.jboss.aerogear.security.otp.api.Base32;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends JpaEntity {

	@Column(name = "full_name", length = 100)
	private String fullName;

	@Column(name = "username", length = 50, nullable = false)
	private String username;
	@Column(name = "email", length = 100, nullable = false)
	private String email;

	@Column(name = "password", length = 64, nullable = false)
	@JsonIgnore
	private String password;

	@Column(name = "phone_number", length = 12)
	private String phoneNumber;

	@Column(name = "staff_id")
	private Long staffId;

	@Column(name = "is_customer")
	private Boolean isCustomer = false;

	@Column(name = "customer_id")
	private Long customerId;
	@Column(name = "is_using_2fa")
	private boolean isUsing2FA;

	@Column(name = "secret", length = 255)
	private String secret;

	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(name = "user_role",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	@JsonIgnore
	private Set<Role> roles = new HashSet<>();

}
