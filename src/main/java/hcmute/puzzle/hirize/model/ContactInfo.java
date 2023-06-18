package hcmute.puzzle.hirize.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContactInfo {
	private String email;
	@JsonProperty("phone_number")
	private String phoneNumber;

	@JsonProperty("street_address")
	private String streetAddress;
	private String city;
	@JsonProperty("zip_code")
	private String zipCode;
	private String country;
}
