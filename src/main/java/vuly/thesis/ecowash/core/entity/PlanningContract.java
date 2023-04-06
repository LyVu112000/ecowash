package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "planning_contract")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class PlanningContract extends JpaEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="planning_id", nullable = false)
    @JsonIgnore
    private Planning planning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="contract_id")
    @JsonIgnore
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    @Column(name = "time_received")
    private String timeReceived;

    @Column(name = "time_delivery")
    private String timeDelivery;

    @Column(name = "bag_number")
    private Integer bagNumber;

    @Column(name = "number_of_looseBags")
    private Integer numberOfLooseBags;

    @Column(name = "note")
    private String note;

    @Column(name = "washing_team")
    private String washingTeam;
}
