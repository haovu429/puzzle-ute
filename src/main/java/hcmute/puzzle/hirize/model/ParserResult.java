package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParserResult {
	String[] skills;
	@JsonProperty("basic_info")
	BasicInfo basicInfo;

	@JsonProperty("contact_info")
	ContactInfo contactInfo;

	@JsonProperty("work_experience")
	WorkExperience workExperience;

	@JsonProperty("education")
	Education[] educations;

	@JsonProperty("achievements_and_honors")
	AchievementsAndHonors[] achievementsAndHonors;
}
