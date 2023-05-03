package hcmute.puzzle.infrastructure.models.enums;

public enum PaymentMethod {
    PAYPAL("PAYPAL"), CAST("CAST");

    private String value;
    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
