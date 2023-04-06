package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "field_staff_link_customer")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DynamicUpdate
public class FieldStaffLinkCustomer extends JpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_staff_id")
    @JsonIgnore
    private Staff fieldStaff;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"fullName", "email", "phoneNumber",
            "address", "note", "tax", "logo", "active"})
    private Customer customer;
}
