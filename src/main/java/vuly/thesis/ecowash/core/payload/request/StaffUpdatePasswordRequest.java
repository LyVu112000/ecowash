package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

@Data
public class StaffUpdatePasswordRequest {

    private String oldPassword;

    private String newPassword;

}