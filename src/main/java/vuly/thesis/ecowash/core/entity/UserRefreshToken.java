package vuly.thesis.ecowash.core.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserRefreshToken extends JpaEntity {

    @NotBlank
//    @Column(nullable = false, unique = true)
    private String token;

    @NonNull
//    @Column(nullable = false)
    private LocalDateTime expiryDate;

//    private Boolean active;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
