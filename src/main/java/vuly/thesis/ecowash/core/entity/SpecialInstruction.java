package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Audited
@Table(name = "special_instruction")
@Builder(toBuilder=true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SpecialInstruction extends JpaEntity {
    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "name", nullable = false)
    private String name;
}
