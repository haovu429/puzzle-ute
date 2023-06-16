package hcmute.puzzle.infrastructure.models.enums;

public enum FileCategory {
    IMAGE_AVATAR("IMAGE_AVATAR"),
    IMAGE_BLOG("IMAGE_BLOG"),
    IMAGE_COMPANY("IMAGE_COMPANY"),
    IMAGE_CATEGORY("IMAGE_CATEGORY"),
    THUMBNAIL_BLOGPOST("THUMBNAIL_BLOGPOST");

    private String value;
    FileCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
