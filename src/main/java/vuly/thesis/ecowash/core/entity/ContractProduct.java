package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "contract_products")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DynamicUpdate
public class ContractProduct extends JpaEntity {
    @ManyToOne
    @JoinColumn(name="contract_id")
    @JsonIgnore
    private Contract contract;

    @ManyToOne
    @JoinColumn(name="product_item_id")
    private ProductItem productItem;

    @Column(name = "note")
    private String note;

    @Column(name = "is_common_item")
    private boolean isCommonProduct;
}
