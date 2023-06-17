package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.ExtraInfoDto;
import hcmute.puzzle.infrastructure.entities.ExtraInfo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExtraInfoMapper {

	ExtraInfoMapper INSTANCE = Mappers.getMapper(ExtraInfoMapper.class);

	ExtraInfoDto extraInfoToExtraInfoDto(ExtraInfo extraInfo);

	ExtraInfo extraInfoDtoToExtraInfo(ExtraInfoDto extraInfoDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateExtraInfoFromExtraInfoDto(ExtraInfoDto extraInfoDto, @MappingTarget ExtraInfo extraInfo);
}
