package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;
import vuly.thesis.ecowash.core.entity.type.ReceiptStatus;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "received_receipt_history")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReceivedReceiptHistory extends JpaEntity {
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;

    @Column(name = "received_id")
    private Long receivedId;


}
