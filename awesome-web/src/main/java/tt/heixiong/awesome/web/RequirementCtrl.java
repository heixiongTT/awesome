package tt.heixiong.awesome.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tt.heixiong.awesome.api.RequirementApi;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.mapper.RequirementMapper;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;
import tt.heixiong.awesome.service.RequirementService;

import javax.validation.Valid;
import java.util.List;

@RestController
@ResponseBody
public class RequirementCtrl implements RequirementApi {

    private final RequirementService requirementService;
    private final RequirementMapper requirementMapper;

    public RequirementCtrl(RequirementService requirementService, RequirementMapper requirementMapper) {
        this.requirementService = requirementService;
        this.requirementMapper = requirementMapper;
    }

    @Override
    public RequirementDto createRequirement(@Valid @RequestBody RequirementCreateReq req) {
        Requirement requirement = requirementMapper.toEntity(req);
        return requirementMapper.toDto(requirementService.createRequirement(requirement));
    }

    @Override
    public List<RequirementDto> listRequirements(@RequestParam(value = "status", required = false) String status,
                                                 @RequestParam(value = "creator", required = false) String creator) {
        return requirementMapper.toDtoList(requirementService.listRequirements(status, creator));
    }

    @Override
    public RequirementDto getRequirement(Long id) {
        return requirementService.getRequirement(id)
                .map(requirementMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found"));
    }

    @Override
    public RequirementDto updateRequirementStatus(@Valid @RequestBody RequirementUpdateStatusReq req) {
        return requirementService.updateRequirementStatus(req.getId(), req.getStatus())
                .map(requirementMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found"));
    }

    @Override
    public void deleteRequirement(Long id) {
        requirementService.deleteRequirement(id);
    }

}
