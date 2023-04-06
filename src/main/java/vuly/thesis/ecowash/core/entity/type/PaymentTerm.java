package vuly.thesis.ecowash.core.entity.type;

public enum PaymentTerm {
    CASH("CASH"),
    BANK_TRANSFER("BANK_TRANSFER");
    private String code;

    PaymentTerm(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}