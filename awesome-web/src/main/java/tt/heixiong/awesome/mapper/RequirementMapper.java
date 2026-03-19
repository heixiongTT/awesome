package tt.heixiong.awesome.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.req.RequirementCreateReq;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequirementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Requirement toEntity(RequirementCreateReq req);

    RequirementDto toDto(Requirement requirement);

    List<RequirementDto> toDtoList(List<Requirement> requirements);
}
