package vuly.thesis.ecowash.core.repository.core;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.entity.User;
import vuly.thesis.ecowash.core.entity.UserRefreshToken;

import java.util.Optional;

@Repository
public interface UserRefreshTokenRepository extends CrudRepository<UserRefreshToken, Long> {

    Optional<UserRefreshToken> findByUserAndToken(User user, String token);
    void deleteAllByUserId(Long userId);

}