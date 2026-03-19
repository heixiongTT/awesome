package tt.heixiong.awesome.mapper;

import org.springframework.stereotype.Component;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.req.RequirementCreateReq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RequirementMapper {

    public Requirement toEntity(RequirementCreateReq req) {
        if (req == null) {
            return null;
        }
        Requirement requirement = new Requirement();
        requirement.setTitle(req.getTitle());
        requirement.setDescription(req.getDescription());
        requirement.setPriority(req.getPriority());
        requirement.setCreator(req.getCreator());
        requirement.setStatus(req.getStatus());
        return requirement;
    }

    public RequirementDto toDto(Requirement requirement) {
        if (requirement == null) {
            return null;
        }
        RequirementDto dto = new RequirementDto();
        dto.setId(requirement.getId());
        dto.setTitle(requirement.getTitle());
        dto.setDescription(requirement.getDescription());
        dto.setPriority(requirement.getPriority());
        dto.setStatus(requirement.getStatus());
        dto.setCreator(requirement.getCreator());
        dto.setCreatedAt(requirement.getCreatedAt());
        dto.setUpdatedAt(requirement.getUpdatedAt());
        return dto;
    }

    public List<RequirementDto> toDtoList(List<Requirement> requirements) {
        if (requirements == null) {
            return Collections.emptyList();
        }
        List<RequirementDto> result = new ArrayList<RequirementDto>(requirements.size());
        for (Requirement requirement : requirements) {
            result.add(toDto(requirement));
        }
        return result;
    }
}
