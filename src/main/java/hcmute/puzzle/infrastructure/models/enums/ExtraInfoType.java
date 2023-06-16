package hcmute.puzzle.infrastructure.models.enums;

public enum ExtraInfoType {
	POSITION("POSITION"), SKILL("SKILL"), SERVICE("SERVICE");

	private String value;
	ExtraInfoType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
