package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;
import vuly.thesis.ecowash.core.entity.type.ContractStatus;
import vuly.thesis.ecowash.core.entity.type.PaymentTerm;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Audited
@Table(name = "contract")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contract extends JpaEntity {
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "valid_date")
    private Instant validDate;

    @Column(name = "expired_date")
    private Instant expiredDate;

    @Column(name = "representative")
    private String representative;

    @Column(name = "representative_position")
    private String representativePosition;

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_id")
    private PaidBy paidBy;

    @Column(name = "payment_term")
    @Enumerated(EnumType.STRING)
    private PaymentTerm paymentTerm = PaymentTerm.CASH;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status = ContractStatus.WAITING;

    @OneToMany(mappedBy="contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContractTime> contractTimes;

    @OneToMany(mappedBy="contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContractProduct> contractProducts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "tax")
    private String tax;

    @Column(name = "sequence_number")
    private int sequenceNumber;

    @Column(name = "active", columnDefinition = "tinyint(1) default true")
    private boolean active = true;
    @Column(name = "is_extend", columnDefinition = "tinyint(1) default false")
    private boolean isExtend = false;
}
