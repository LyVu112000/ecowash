package vuly.thesis.ecowash.core.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameConstraintValidator implements ConstraintValidator<ValidUsername, String> {

    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,30}$";

    @Override
    public void initialize(final ValidUsername arg0) {

    }

    @Override
    public boolean isValid(final String username, final ConstraintValidatorContext context) {
        return (validateUsername(username));
    }

    private boolean validateUsername(final String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
