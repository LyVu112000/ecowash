package vuly.thesis.ecowash.core.entity;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Audited
@Table(name = "department")
@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class Department extends JpaEntity {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "note")
    private String note;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "phone_number")
    private String phoneNumber;

}

