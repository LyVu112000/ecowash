package vuly.thesis.ecowash.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.RoleStatus;
import javax.persistence.*;
import java.security.Permission;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "role", uniqueConstraints = {@UniqueConstraint(columnNames = {"name","tenant_id"}), @UniqueConstraint(columnNames = {"label","tenant_id"}) })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends JpaEntity {

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "label", length = 200)
    private String label;


    @ManyToMany(mappedBy = "roles")
    Set<User> users;

}
