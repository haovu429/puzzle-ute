package hcmute.puzzle.infrastructure.models.enums;

public enum SeniorityType {
	Intern("Intern"),
	Junior("Junior"),
	Mid("Mid"), Senior("Senior"),
	Lead("Senior"),
	C_LEVEL_VIP("C Level / VP");

	private final String value;

	public String getValue() {
		return value;
	}

	SeniorityType(String value) {
		this.value = value;
	}
}
