package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "received_link_delivery")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DynamicUpdate
public class ReceivedLinkDelivery extends JpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_receipt_id")
    @JsonIgnore
    private ReceivedReceipt receivedReceipt;

    @ManyToOne
    @JoinColumn(name = "delivery_receipt_id")
    @JsonIgnoreProperties({"productType","customer", "deliveryDate", "note",
            "numberRoom", "staffCheck", "checkDate", "status", "weight", "bagNumber", "numberOfLooseBags", "isAutoGen", "isFlagError",
            "confirmNote", "sequenceNumber", "createdSourceType", "itemDeliveryList", "deliveryLinkReceivedList",
            "imageReceipts", "autoGen","flagError", "signatureStaff"})
    private DeliveryReceipt deliveryReceipt;
}
