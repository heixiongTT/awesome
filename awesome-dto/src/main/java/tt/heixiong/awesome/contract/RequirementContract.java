package tt.heixiong.awesome.contract;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tt.heixiong.awesome.common.ApiResponse;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;

import javax.validation.Valid;
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
