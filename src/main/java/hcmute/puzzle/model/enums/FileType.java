package hcmute.puzzle.model.enums;

public enum FileType {
    IMAGE("image"), VIDEO("video");

    private String value;
    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
