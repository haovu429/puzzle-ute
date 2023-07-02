package hcmute.puzzle.infrastructure.models.enums.converter_enum;

import hcmute.puzzle.infrastructure.models.enums.JsonDataType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class JsonDataTypeConverter implements AttributeConverter<JsonDataType, String> {
	@Override
	public String convertToDatabaseColumn(JsonDataType attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	@Override
	public JsonDataType convertToEntityAttribute(String value) {
		if (value == null) {
			return null;
		}

		return Stream.of(JsonDataType.values())
					 .filter(c -> c.getValue().equals(value))
					 .findFirst()
					 .orElseThrow(IllegalArgumentException::new);
	}
}
