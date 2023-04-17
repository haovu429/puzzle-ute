package hcmute.puzzle.model.enums.converter_enum;

import hcmute.puzzle.model.enums.FileCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
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
