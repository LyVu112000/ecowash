package vuly.thesis.ecowash.core.entity.type;

public enum Status {

    ACTIVE("ACTIVE"),
    DEACTIVE("DEACTIVE");

    private String code;

    Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
