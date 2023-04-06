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
@Table(name = "special_instruction_receipt")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DynamicUpdate
public class SpecialInstructionOfReceipt extends JpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="received_receipt_id")
    @JsonIgnore
    private ReceivedReceipt receivedReceipt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "delivery_receipt_id")
//    @JsonIgnore
//    private DeliveryReceipt deliveryReceipt;

    @ManyToOne
    @JoinColumn(name="special_instruction_id")
    private SpecialInstruction specialInstruction;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id")
//    @JsonIgnore
//    private Customer customer;
}
