package vuly.thesis.ecowash.core.entity.type;

public enum ContractStatus {

    WAITING("WAITING"),
    APPROVED("APPROVED"),
    CANCEL("CANCEL");
    private String code;

    ContractStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
