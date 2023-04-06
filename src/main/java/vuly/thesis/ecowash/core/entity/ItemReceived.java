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
@Table(name = "item_received")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DynamicUpdate
public class ItemReceived extends JpaEntity {

    @ManyToOne
    @JoinColumn(name="received_receipt_id")
    @JsonIgnore
    private ReceivedReceipt receivedReceipt;

    @ManyToOne
    @JoinColumn(name="delivery_receipt_id")
    @JsonIgnoreProperties({"productType","customer", "deliveryDate", "note",
            "numberRoom", "staffCheck", "checkDate", "status", "weight", "bagNumber", "numberOfLooseBags", "isAutoGen", "isFlagError",
            "confirmNote", "sequenceNumber", "createdSourceType", "itemDeliveryList", "deliveryLinkReceivedList",
            "imageReceipts", "autoGen","flagError", "signatureStaff"})
    private DeliveryReceipt deliveryReceipt;
    @ManyToOne
    @JoinColumn(name="product_item_id")
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

}
