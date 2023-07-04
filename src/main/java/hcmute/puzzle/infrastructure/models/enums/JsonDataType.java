package hcmute.puzzle.infrastructure.models.enums;

public enum JsonDataType {
	HIRIZE_AI_MATCHER("HIRIZE_AI_MATCHER"), HIRIZE_IQ("HIRIZE_IQ");

	private String value;
	JsonDataType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
