package tt.heixiong.awesome.web;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tt.heixiong.awesome.common.ApiResponse;
import tt.heixiong.awesome.contract.RequirementContract;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.mapper.RequirementMapper;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;
import tt.heixiong.awesome.service.RequirementService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "Requirement Controller")
@RestController
@ResponseBody
@RequestMapping("/requirements")
public class RequirementCtrl implements RequirementContract {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private final RequirementService requirementService;
    private final RequirementMapper requirementMapper;
    private final HttpServletRequest request;

    public RequirementCtrl(RequirementService requirementService,
                           RequirementMapper requirementMapper,
                           HttpServletRequest request) {
        this.requirementService = requirementService;
        this.requirementMapper = requirementMapper;
        this.request = request;
    }

    @Override
    public ApiResponse<RequirementDto> createRequirement(@Valid @RequestBody RequirementCreateReq req) {
        Requirement requirement = requirementMapper.toEntity(req);
        return success(requirementMapper.toDto(requirementService.createRequirement(requirement)));
    }

    @Override
    public ApiResponse<List<RequirementDto>> listRequirements(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "creator", required = false) String creator) {
        return success(requirementMapper.toDtoList(requirementService.listRequirements(status, creator)));
    }

    @Override
    public ApiResponse<RequirementDto> getRequirement(Long id) {
        RequirementDto requirementDto = requirementService.getRequirement(id)
                .map(requirementMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found"));
        return success(requirementDto);
    }

    @Override
    public ApiResponse<RequirementDto> updateRequirementStatus(@Valid @RequestBody RequirementUpdateStatusReq req) {
        RequirementDto requirementDto = requirementService.updateRequirementStatus(req.getId(), req.getStatus())
                .map(requirementMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found"));
        return success(requirementDto);
    }

    @Override
    public ApiResponse<Void> deleteRequirement(Long id) {
        requirementService.deleteRequirement(id);
        return success(null);
    }

    private <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data, request.getHeader(TRACE_ID_HEADER));
    }
}
