package hcmute.puzzle.infrastructure.models.enums.converter_enum;

import hcmute.puzzle.infrastructure.models.enums.FileType;

//import javax.persistence.AttributeConverter;
//import javax.persistence.Converter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class FileTypeConverter implements AttributeConverter<FileType, String> {

    @Override
    public String convertToDatabaseColumn(FileType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public FileType convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Stream.of(FileType.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
