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
@Table(name = "received_receipt")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReceivedReceipt extends JpaEntity {

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "reference_code")
    private String referenceCode;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    @JsonIgnoreProperties({"validDate", "expiredDate", "representative", "representativePosition", "note", "paidBy",
            "paymentTerm", "status", "contractTimes","contractProducts", "customer", "sequenceNumber"})
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "received_date")
    private Instant receivedDate;

    @Column(name = "delivery_date")
    private Instant deliveryDate;
    @Column(name = "finish_date")
    private Instant finishDate;

    @Column(name = "is_rewash", columnDefinition = "tinyint(1) default false")
    private boolean isRewash = false;

    @Column(name = "note")
    private String note;
    @Column(name = "fs_note")
    private String fsNote;
    @Column(name = "number_room")
    private String numberRoom;

//    @Column(name = "special_instruction")
//    private String specialInstruction;
//    private SpecialInstruction specialInstruction;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status = ReceiptStatus.WAITING;

    @Column(name = "weight")
    private String weight;

    @Column(name = "bag_number")
    private int bagNumber = 0;

    @Column(name = "delivery_bag_number")
    private int deliveryBagNumber = 0;

    @Column(name = "number_of_loose_bags")
    private String numberOfLooseBags;

    @Column(name = "number_of_check_bags")
    private int numberOfCheckBags = 0;

    @Column(name = "signature_customer")
    @JsonRawValue
    private String signatureCustomer;

    @Column(name = "signature_staff")
    @JsonRawValue
    private String signatureStaff;

    @Column(name = "sequence_number")
    private int sequenceNumber;

    @Column(name = "is_flag_error", columnDefinition = "tinyint(1) default false")
    private boolean isFlagError = false;

    @Column(name = "mark_error", columnDefinition = "tinyint(1) default false")
    private boolean isMarkError = false;

    @Column(name = "has_delivery_receipt_done", columnDefinition = "tinyint(1) default false")
    private boolean hasDeliveryReceiptDone = false;

    @Column(name = "is_express", columnDefinition = "tinyint(1) default false")
    private boolean isExpress = false;

    @Column(name = "image_paths")
    private String imagePaths;

    @Column(name = "confirm_note")
    private String confirmNote;
    @Column(name = "cancel_note")
    private String cancelNote;
    @Column(name = "is_auto_gen", columnDefinition = "tinyint(1) default false")
    private boolean isAutoGen = false;
    @Column(name = "is_debt_closing", columnDefinition = "tinyint(1) default false")
    private boolean isDebtClosing = false;
    @Column(name = "created_source_type")
    @Enumerated(EnumType.STRING)
    private CreatedSourceType createdSourceType = CreatedSourceType.PORTAL;

    @OneToMany(mappedBy="receivedReceipt", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<ItemReceived> itemReceivedList = new ArrayList<>();

    @OneToMany(mappedBy="receivedReceipt",  cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    List<ReceivedLinkDelivery> receivedLinkDeliveries = new ArrayList<>();;

    @OneToMany(mappedBy="receivedReceipt",  cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    List<SpecialInstructionOfReceipt> specialInstructionOfReceipts = new ArrayList<>();;

    @OneToMany(mappedBy="receivedReceipt",  cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    List<ImageReceipt> imageReceipts = new ArrayList<>();

    // truck and driver

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
}
