package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "delivery_link_received")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DynamicUpdate
public class DeliveryLinkReceived extends JpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="received_receipt_id")
    @JsonIgnoreProperties({"productType","customer", "contract", "deliveryDate", "note", "isRewash",
            "numberRoom", "status", "weight", "bagNumber", "deliveryBagNumber", "numberOfLooseBags", "isAutoGen", "isFlagError",
            "signatureCustomer", "signatureStaff",
            "confirmNote", "sequenceNumber", "createdSourceType", "itemReceivedList", "receivedLinkDeliveries",
            "specialInstructionOfReceipts", "imageReceipts", "autoGen","flagError"})
    ReceivedReceipt receivedReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_receipt_id")
    @JsonIgnore
    DeliveryReceipt deliveryReceipt;

    public ReceivedReceipt getReceivedReceipt() {
        return receivedReceipt;
    }
}
