package hcmute.puzzle.infrastructure.models.enums;

public enum FileType {
    IMAGE("image"), VIDEO("video"), PDF("pdf");

    private String value;
    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
