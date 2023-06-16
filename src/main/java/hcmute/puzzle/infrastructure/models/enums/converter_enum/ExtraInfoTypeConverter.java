package hcmute.puzzle.infrastructure.models.enums.converter_enum;

import hcmute.puzzle.infrastructure.models.enums.ExtraInfoType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class ExtraInfoTypeConverter implements AttributeConverter<ExtraInfoType, String> {

    @Override
    public String convertToDatabaseColumn(ExtraInfoType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public ExtraInfoType convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Stream.of(ExtraInfoType.values())
                     .filter(c -> c.getValue().equals(value))
                     .findFirst()
                     .orElseThrow(IllegalArgumentException::new);
    }
}
