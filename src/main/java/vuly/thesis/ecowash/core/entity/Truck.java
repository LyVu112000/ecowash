package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.envers.Audited;
import vuly.thesis.ecowash.core.entity.type.Status;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Audited
@Table(name = "truck")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Truck extends JpaEntity {

    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "activated_time")
    private Instant activatedTime;

    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private boolean deleted = false;
}

