package vuly.thesis.ecowash.core.entity.type;

public enum PlanningStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    CANCEL("CANCEL");

    private String code;

    PlanningStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
