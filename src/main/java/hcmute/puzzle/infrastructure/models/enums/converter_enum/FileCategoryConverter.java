package hcmute.puzzle.infrastructure.models.enums.converter_enum;

import hcmute.puzzle.infrastructure.models.enums.FileCategory;

//import javax.persistence.AttributeConverter;
//import javax.persistence.Converter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

// https://www.baeldung.com/jpa-persisting-enums-in-jpa

@Converter(autoApply = true)
public class FileCategoryConverter implements AttributeConverter<FileCategory, String> {

    @Override
    public String convertToDatabaseColumn(FileCategory attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public FileCategory convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Stream.of(FileCategory.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
