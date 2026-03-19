package tt.heixiong.awesome.web;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tt.heixiong.awesome.contract.RequirementContract;
import tt.heixiong.awesome.api.RequirementApi;
import tt.heixiong.awesome.common.ApiResponse;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.exception.ResourceNotFoundException;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;
import tt.heixiong.awesome.service.RequirementService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ResponseBody
public class RequirementCtrl implements RequirementContract {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private final RequirementService requirementService;
    private final HttpServletRequest request;

    public RequirementCtrl(RequirementService requirementService, HttpServletRequest request) {
        this.requirementService = requirementService;
        this.request = request;
    }

    @Override
    public ApiResponse<RequirementDto> createRequirement(@Valid @RequestBody RequirementCreateReq req) {
        Requirement requirement = new Requirement();
        requirement.setTitle(req.getTitle());
        requirement.setDescription(req.getDescription());
        requirement.setPriority(req.getPriority());
        requirement.setCreator(req.getCreator());
        requirement.setStatus(req.getStatus());
        return ApiResponse.success(toDto(requirementService.createRequirement(requirement)), getTraceId());
    }

    @Override
    public ApiResponse<List<RequirementDto>> listRequirements(@RequestParam(value = "status", required = false) String status,
                                                              @RequestParam(value = "creator", required = false) String creator) {
        List<RequirementDto> requirements = requirementService.listRequirements(status, creator)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ApiResponse.success(requirements, getTraceId());
    }

    @Override
    public ApiResponse<RequirementDto> getRequirement(Long id) {
        Requirement requirement = requirementService.getRequirement(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
        return ApiResponse.success(toDto(requirement), getTraceId());
    }

    @Override
    public ApiResponse<RequirementDto> updateRequirementStatus(@Valid @RequestBody RequirementUpdateStatusReq req) {
        Requirement requirement = requirementService.updateRequirementStatus(req.getId(), req.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
        return ApiResponse.success(toDto(requirement), getTraceId());
    }

    @Override
    public ApiResponse<Void> deleteRequirement(Long id) {
        requirementService.deleteRequirement(id);
        return ApiResponse.success(null, getTraceId());
    }

    private RequirementDto toDto(Requirement requirement) {
        RequirementDto dto = new RequirementDto();
        BeanUtils.copyProperties(requirement, dto);
        return dto;
    }

    private String getTraceId() {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        return traceId != null && traceId.trim().length() > 0 ? traceId : UUID.randomUUID().toString();
    }
}
