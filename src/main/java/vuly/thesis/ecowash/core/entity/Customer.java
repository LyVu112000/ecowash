package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Audited
@Table(name = "customer")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer extends JpaEntity {
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "note")
    private String note;

    @Column(name = "tax")
    private String tax;

    @Column(name = "logo")
    private String logo;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
