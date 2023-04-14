package vuly.thesis.ecowash.core.exception;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppExternalExceptionResponse {
    private String code;
    private String message;
}
