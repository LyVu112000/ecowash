package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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

	@ManyToOne
	private Role role;
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private UserRefreshToken refreshToken;

	public void addRefreshToken(UserRefreshToken refreshToken) {
		this.refreshToken = refreshToken;
		refreshToken.setUser(this);
	}
}
