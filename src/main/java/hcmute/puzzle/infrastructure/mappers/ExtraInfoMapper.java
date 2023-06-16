package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.ExtraInfoDto;
import hcmute.puzzle.infrastructure.entities.ExtraInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExtraInfoMapper {

	ExtraInfoMapper INSTANCE = Mappers.getMapper(ExtraInfoMapper.class);

	ExtraInfoDto extraInfoToExtraInfoDto(ExtraInfo extraInfo);

	ExtraInfo extraInfoDtoToExtraInfo(ExtraInfoDto extraInfoDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateExtraInfoFromExtraInfoDto(ExtraInfoDto extraInfoDto, @MappingTarget ExtraInfo extraInfo);
}
