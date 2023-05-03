package hcmute.puzzle.infrastructure.models.enums;

public enum FileCategory {
    IMAGE_AVATAR("IMAGE_AVATAR"), IMAGE_BLOG("IMAGE_BLOG"), IMAGE_COMPANY("IMAGE_COMPANY");

    private String value;
    FileCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
