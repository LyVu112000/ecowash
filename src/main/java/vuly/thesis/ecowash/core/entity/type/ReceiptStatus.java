package vuly.thesis.ecowash.core.entity.type;

public enum ReceiptStatus {

    WAITING("WAITING"),
    CUSTOMER_RECEIVED("CUSTOMER_RECEIVED"),
    STAFF_RECEIVED("STAFF_RECEIVED"),
    WAITING_RANDOM_CHECK("WAITING_RANDOM_CHECK"),
    WASHING("WASHING"),
    PACKING("PACKING"),
    WAITING_DELIVERY("WAITING_DELIVERY"),
    DELIVERY("DELIVERY"),
    DONE("DONE"),
    CANCEL("CANCEL"),
    ACCEPT("ACCEPT"),
    REQUEST_RECHECK("REQUEST_RECHECK"),
    DEBT_CLOSING("DEBT_CLOSING");
    private String code;

    ReceiptStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
