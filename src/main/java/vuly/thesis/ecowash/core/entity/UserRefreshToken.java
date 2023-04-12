package vuly.thesis.ecowash.core.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_refresh_token")
public class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String token;

    @Column
    private String jti;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    public UserRefreshToken() {
    }

    public UserRefreshToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.jti = UUID.randomUUID().toString();
    }
}
