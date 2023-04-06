package vuly.thesis.ecowash.core.entity;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "product_item")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductItem extends JpaEntity {
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "note")
    private String note;

    @Column(name = "active", nullable = false)
    private boolean active = true;
    @Column(name = "is_other", nullable = false)
    private boolean isOther = false;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    @ManyToOne
    @JoinColumn(name = "piece_type_id")
    private PieceType pieceType;

    @ManyToOne
    @JoinColumn(name = "product_group_id")
    private ProductGroup productGroup;
}
