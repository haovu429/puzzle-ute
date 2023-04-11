package hcmute.puzzle.model.enums;

public enum FileType {
    IMAGE_AVATAR("IMAGE_AVATAR"), IMAGE_BLOG("IMAGE_BLOG"), IMAGE_COMPANY("IMAGE_COMPANY");

    private String value;
    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
