package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.*;
import org.hibernate.envers.Audited;
import vuly.thesis.ecowash.core.entity.type.Status;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Table(name = "staff")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Staff extends JpaEntity {
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "username",  nullable = false)
    private String username;

    @Column(name = "password",  nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "role")
    private String role;

    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "activated_time")
    private Instant activatedTime;
    @Column(name = "signature")
    @JsonRawValue
    private String signature;

    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private boolean deleted = false;

    @Column(name = "is_customer")
    private Boolean isCustomer = false;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "note")
    private String note;
}

