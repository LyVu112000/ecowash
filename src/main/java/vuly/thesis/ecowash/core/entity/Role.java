package vuly.thesis.ecowash.core.entity;

import lombok.*;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends JpaEntity {

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "label", length = 200)
    private String label;

}
