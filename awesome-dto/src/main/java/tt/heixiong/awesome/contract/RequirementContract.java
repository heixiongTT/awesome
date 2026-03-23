package tt.heixiong.awesome.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tt.heixiong.awesome.common.ApiResponse;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;

import java.util.List;

public interface RequirementContract {

    @PostMapping
    ApiResponse<RequirementDto> createRequirement(@Valid @RequestBody RequirementCreateReq req);

    @GetMapping
    ApiResponse<List<RequirementDto>> listRequirements(@RequestParam(value = "status", required = false) String status,
                                                       @RequestParam(value = "creator", required = false) String creator);

    @GetMapping("/{id}")
    ApiResponse<RequirementDto> getRequirement(@PathVariable("id") Long id);

    @PutMapping("/status")
    ApiResponse<RequirementDto> updateRequirementStatus(@Valid @RequestBody RequirementUpdateStatusReq req);

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteRequirement(@PathVariable("id") Long id);
}
