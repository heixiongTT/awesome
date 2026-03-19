package tt.heixiong.awesome.contract;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;

import java.util.List;

@RequestMapping("/requirements")
public interface RequirementContract {

    @PostMapping
    RequirementDto createRequirement(@Validated @RequestBody RequirementCreateReq req);

    @GetMapping
    List<RequirementDto> listRequirements(@RequestParam(value = "status", required = false) String status,
                                          @RequestParam(value = "creator", required = false) String creator);

    @GetMapping("/{id}")
    RequirementDto getRequirement(@PathVariable("id") Long id);

    @PutMapping("/status")
    RequirementDto updateRequirementStatus(@Validated @RequestBody RequirementUpdateStatusReq req);

    @DeleteMapping("/{id}")
    void deleteRequirement(@PathVariable("id") Long id);
}
