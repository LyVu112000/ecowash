package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;
import vuly.thesis.ecowash.core.entity.type.CreatedSourceType;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "image_receipt")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ImageReceipt extends JpaEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="received_receipt_id")
    @JsonIgnore
    private ReceivedReceipt receivedReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_receipt_id")
    @JsonIgnore
    private DeliveryReceipt deliveryReceipt;

    @Column(name = "image")
    String image;
    @Column(name = "created_source_type")
    @Enumerated(EnumType.STRING)
    private CreatedSourceType createdSourceType = CreatedSourceType.PORTAL;
}
