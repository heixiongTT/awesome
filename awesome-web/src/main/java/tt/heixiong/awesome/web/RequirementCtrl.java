package tt.heixiong.awesome.web;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tt.heixiong.awesome.api.RequirementApi;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;
import tt.heixiong.awesome.service.RequirementService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@ResponseBody
public class RequirementCtrl implements RequirementApi {

    private final RequirementService requirementService;

    public RequirementCtrl(RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @Override
    public RequirementDto createRequirement(@Valid @RequestBody RequirementCreateReq req) {
        Requirement requirement = new Requirement();
        requirement.setTitle(req.getTitle());
        requirement.setDescription(req.getDescription());
        requirement.setPriority(req.getPriority());
        requirement.setCreator(req.getCreator());
        requirement.setStatus(req.getStatus());
        return toDto(requirementService.createRequirement(requirement));
    }

    @Override
    public List<RequirementDto> listRequirements(@RequestParam(value = "status", required = false) String status,
                                                 @RequestParam(value = "creator", required = false) String creator) {
        return requirementService.listRequirements(status, creator)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequirementDto getRequirement(Long id) {
        return requirementService.getRequirement(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found"));
    }

    @Override
    public RequirementDto updateRequirementStatus(@Valid @RequestBody RequirementUpdateStatusReq req) {
        return requirementService.updateRequirementStatus(req.getId(), req.getStatus())
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found"));
    }

    @Override
    public void deleteRequirement(Long id) {
        requirementService.deleteRequirement(id);
    }

    private RequirementDto toDto(Requirement requirement) {
        RequirementDto dto = new RequirementDto();
        BeanUtils.copyProperties(requirement, dto);
        return dto;
    }
}
