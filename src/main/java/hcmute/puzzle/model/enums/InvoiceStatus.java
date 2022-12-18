package hcmute.puzzle.model.enums;

public enum InvoiceStatus {
    UNFINISHED("UNFINISHED"), COMPLETED("COMPLETED");

    private String value;
    InvoiceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
