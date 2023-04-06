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
@Table(name = "item_delivery")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DynamicUpdate
public class ItemDelivery extends JpaEntity {

    @ManyToOne
    @JoinColumn(name = "delivery_receipt_id")
    @JsonIgnore
    private DeliveryReceipt deliveryReceipt;

    @ManyToOne
    @JoinColumn(name = "received_receipt_id")
    @JsonIgnoreProperties({"productType", "contract", "customer", "receivedDate",
            "deliveryDate", "isRewash", "note", "status", "weight", "bagNumber", "numberOfLooseBags", "isAutoGen", "isFlagError",
            "deliveryBagNumber", "signatureCustomer", "signatureStaff", "sequenceNumber", "isMarkError",
            "hasDeliveryReceiptDone", "confirmNote", "autoGen", "flagError", "createdSourceType", "itemReceivedList", "receivedLinkDeliveries",
            "specialInstructionOfReceipts", "imageReceipts", "imageReceipts"})
    private ReceivedReceipt receivedReceipt;
    @ManyToOne
    @JoinColumn(name = "product_item_id")
    @JsonIgnoreProperties({"note", "active", "productType", "productGroup"})
    private ProductItem productItem;
    @Column(name = "number_received")
    private int numberReceived = 0;
    @ManyToOne
    @JoinColumn(name = "laundry_form_id")
    private LaundryForm laundryForm;
    @Column(name = "note")
    private String note;

    @Column(name = "random_number_check")
    private int randomNumberCheck = 0;

    @Column(name = "number_after_production")
    private int numberAfterProduction = 0;
    @Column(name = "number_delivery")
    private int numberDelivery = 0;
    @Column(name = "number_delivery_actual")
    private int numberDeliveryActual = 0;
}
