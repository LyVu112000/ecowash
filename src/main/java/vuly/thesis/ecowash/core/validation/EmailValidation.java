package vuly.thesis.ecowash.core.validation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidation {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public boolean isValid(final String email) {
        return Pattern.compile(EMAIL_PATTERN)
                .matcher(email)
                .matches();
    }
}
