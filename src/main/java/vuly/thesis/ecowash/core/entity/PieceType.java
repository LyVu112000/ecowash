package vuly.thesis.ecowash.core.entity;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Audited
@Table(name = "piece_type")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class PieceType extends JpaEntity {
    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "name", nullable = false)
    private String name;
}
