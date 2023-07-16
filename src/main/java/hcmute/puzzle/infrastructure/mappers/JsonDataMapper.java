package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.news.JsonDataDto;
import hcmute.puzzle.infrastructure.dtos.request.CreateJsonDataTypeCvRequest;
import hcmute.puzzle.infrastructure.dtos.request.UpdateJsonDataTypeCvRequest;
import hcmute.puzzle.infrastructure.entities.JsonData;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface JsonDataMapper {

	JsonDataMapper INSTANCE = Mappers.getMapper(JsonDataMapper.class);


	JsonDataDto jsonDataToJsonDataDto(JsonData jsonData);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "hirizeId", ignore = true)
	@Mapping(target = "applicationId", ignore = true)
	@Mapping(target = "type", ignore = true)
	JsonData createJsonDataTypeCvRequestToJsonData(CreateJsonDataTypeCvRequest createJsonDataTypeCvRequest);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "hirizeId", ignore = true)
	@Mapping(target = "applicationId", ignore = true)
	@Mapping(target = "type", ignore = true)
	void updateJsonDataFromJsonDataUpdateRequest(
			UpdateJsonDataTypeCvRequest updateJsonDataTypeCvRequest, @MappingTarget JsonData jsonData);
}
