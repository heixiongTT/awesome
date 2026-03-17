package tt.heixiong.awesome.web;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tt.heixiong.awesome.api.RequirementApi;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;
import tt.heixiong.awesome.service.RequirementService;

import java.util.ArrayList;
import java.util.List;

@Controller
@ResponseBody
public class RequirementCtrl implements RequirementApi {

    @Autowired
    private RequirementService requirementService;

    @Override
    public RequirementDto createRequirement(@Validated @RequestBody RequirementCreateReq req) {
        Requirement requirement = new Requirement();
        requirement.setTitle(req.getTitle());
        requirement.setDescription(req.getDescription());
        requirement.setPriority(req.getPriority());
        requirement.setCreator(req.getCreator());
        return toDto(requirementService.createRequirement(requirement));
    }

    @Override
    public List<RequirementDto> listRequirements() {
        List<Requirement> requirements = requirementService.listRequirements();
        List<RequirementDto> result = new ArrayList<RequirementDto>();
        requirements.forEach(r -> result.add(toDto(r)));
        return result;
    }

    @Override
    public RequirementDto getRequirement(@PathVariable("id") Long id) {
        Requirement requirement = requirementService.getRequirement(id);
        if (requirement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found");
        }
        return toDto(requirement);
    }

    @Override
    public RequirementDto updateRequirementStatus(@Validated @RequestBody RequirementUpdateStatusReq req) {
        Requirement requirement = requirementService.updateRequirementStatus(req.getId(), req.getStatus());
        if (requirement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found");
        }
        return toDto(requirement);
    }

    @Override
    public void deleteRequirement(@PathVariable("id") Long id) {
        requirementService.deleteRequirement(id);
    }

    private RequirementDto toDto(Requirement requirement) {
        RequirementDto dto = new RequirementDto();
        BeanUtils.copyProperties(requirement, dto);
        return dto;
    }
}
