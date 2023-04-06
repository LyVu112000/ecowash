package vuly.thesis.ecowash.core.entity;

import lombok.*;
import org.hibernate.envers.Audited;
import vuly.thesis.ecowash.core.entity.type.PlanningStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Table(name = "planning", uniqueConstraints = {@UniqueConstraint(columnNames = {"tenant_id", "code"})})
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class Planning extends JpaEntity {

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "from_date")
    private Instant fromDate;

    @Column(name = "to_date")
    private Instant toDate;

    @Enumerated(EnumType.STRING)
    private PlanningStatus status;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @OneToMany(mappedBy="planning", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanningContract> planningContracts = new ArrayList<>();

}
