package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePasswordRequest {

    private String username;

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;

}

