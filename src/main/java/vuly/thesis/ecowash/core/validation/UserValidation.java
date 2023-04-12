package vuly.thesis.ecowash.core.validation;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.repository.core.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidation {
    private static final Logger logger = LoggerFactory.getLogger(UserValidation.class);

    @Autowired
    private UserRepository userRepository;


    public boolean checkExistedUsername(String username) {
        logger.info("handle check username is existed");
        if (userRepository.existsByUsername(username)) {
            return true;
        }

        return false;
    }
}
