package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.*;
import org.hibernate.envers.Audited;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;
import vuly.thesis.ecowash.core.entity.type.ReceiptStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Table(name = "delivery_receipt", uniqueConstraints = {@UniqueConstraint(columnNames = {"tenant_id", "code"})})
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DeliveryReceipt extends JpaEntity {
    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "delivery_date")
    private Instant deliveryDate;

    @Column(name = "note")
    private String note;
    @Column(name = "number_room")
    private String numberRoom;

    @Column(name = "staff_check")
    private String staffCheck;

    @Column(name = "check_date")
    private Instant checkDate;

    @Column(name = "finish_date")
    private Instant finishDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status = ReceiptStatus.WAITING;
    @Column(name = "weight")
    private float weight;
    @Column(name = "bag_number")
    private int bagNumber = 0;
    @Column(name = "number_of_loose_bags")
    private String numberOfLooseBags;
    @Column(name = "cancel_note")
    private String cancelNote;
    @Column(name = "is_auto_gen", columnDefinition = "tinyint(1) default false")
    private boolean isAutoGen = false;

    @Column(name = "is_flag_error", columnDefinition = "tinyint(1) default false")
    private boolean isFlagError = false;

    @Column(name = "mark_error", columnDefinition = "tinyint(1) default false")
    private boolean isMarkError = false;

    @Column(name = "is_express", columnDefinition = "tinyint(1) default false")
    private boolean isExpress = false;

    @Column(name = "confirm_note")
    private String confirmNote;

    @Column(name = "sequence_number")
    private int sequenceNumber;

    @Column(name = "signature_staff")
    @JsonRawValue
    private String signatureStaff;

    @Column(name = "signature_customer")
    @JsonRawValue
    private String signatureCustomer;

    @Column(name = "created_source_type")
    @Enumerated(EnumType.STRING)
    private CreatedSourceType createdSourceType = CreatedSourceType.PORTAL;

    @OneToMany(mappedBy="deliveryReceipt", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    List<ItemDelivery> itemDeliveryList = new ArrayList<>();

    @OneToMany(mappedBy="deliveryReceipt",cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    List<DeliveryLinkReceived> deliveryLinkReceivedList = new ArrayList<>();

//    @OneToMany(mappedBy="deliveryReceipt", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
//    List<SpecialInstructionOfReceipt> specialInstructionOfReceipts = new ArrayList<>();

    @OneToMany(mappedBy="deliveryReceipt", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    List<ImageReceipt> imageReceipts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    @JsonIgnoreProperties({"staff", "status"})
    private Truck truck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @JsonIgnoreProperties({"fieldStaffLinkCustomers", "department", "username", "activatedTime", "status"})
    private Staff driver;

    @Column(name = "receipt_image")
    private String receiptImage;

    @Column(name = "is_gen_by_debt", columnDefinition = "tinyint(1) default false")
    private boolean isGenByDebt = false;
}
