package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "contract_time")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class ContractTime extends JpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="contract_id", nullable = false)
    @JsonIgnore
    private Contract contract;

    @Column(name = "time_received")
    private String timeReceived;

    @Column(name = "time_delivery")
    private String timeDelivery;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

}
