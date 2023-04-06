package vuly.thesis.ecowash.core.entity.type;

public enum CreatedSourceType {
    CUSTOMER_APP("CUSTOMER_APP"),
    STAFF_APP("STAFF_APP"),
    PORTAL("PORTAL");
    private String code;

    CreatedSourceType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}