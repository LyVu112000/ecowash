package vuly.thesis.ecowash.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppExternalException extends RuntimeException {

    private String code;
    private String message;

    public AppExternalException(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
