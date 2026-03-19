package tt.heixiong.awesome.api;

import io.swagger.annotations.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tt.heixiong.awesome.dto.ApiErrorResponse;
import tt.heixiong.awesome.dto.RequirementDto;
import tt.heixiong.awesome.req.RequirementCreateReq;
import tt.heixiong.awesome.req.RequirementUpdateStatusReq;

import java.util.List;

@Api(tags = "Requirement")
@FeignClient(value = "awesome")
@RequestMapping("/requirements")
public interface RequirementApi {

    @ApiOperation(value = "创建需求", notes = "创建一条新的需求记录。")
    @ApiResponses({
            @ApiResponse(code = 200, message = "创建成功", response = RequirementDto.class),
            @ApiResponse(code = 400, message = "请求参数校验失败", response = ApiErrorResponse.class)
    })
    @PostMapping
    RequirementDto createRequirement(
            @ApiParam(value = "创建需求请求体", required = true,
                    example = "{\"title\":\"补齐 OpenAPI 文档\",\"description\":\"补充 schema 与示例\",\"priority\":\"HIGH\",\"creator\":\"codex\",\"status\":\"TODO\"}")
            @Validated @RequestBody RequirementCreateReq req);

    @ApiOperation(value = "查询需求列表", notes = "按状态、创建人筛选需求列表；不传时返回全部。")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = RequirementDto.class, responseContainer = "List")
    })
    @GetMapping
    List<RequirementDto> listRequirements(
            @ApiParam(value = "需求状态过滤", example = "TODO")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(value = "创建人过滤", example = "codex")
            @RequestParam(value = "creator", required = false) String creator);

    @ApiOperation(value = "查询需求详情", notes = "按 ID 返回单条需求详情。")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = RequirementDto.class),
            @ApiResponse(code = 404, message = "需求不存在", response = ApiErrorResponse.class)
    })
    @GetMapping("/{id}")
    RequirementDto getRequirement(@ApiParam(value = "需求 ID", example = "1", required = true) @PathVariable("id") Long id);

    @ApiOperation(value = "更新需求状态", notes = "仅更新需求的状态字段。")
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功", response = RequirementDto.class),
            @ApiResponse(code = 400, message = "请求参数校验失败", response = ApiErrorResponse.class),
            @ApiResponse(code = 404, message = "需求不存在", response = ApiErrorResponse.class)
    })
    @PutMapping("/status")
    RequirementDto updateRequirementStatus(
            @ApiParam(value = "更新状态请求体", required = true,
                    example = "{\"id\":1,\"status\":\"DONE\"}")
            @Validated @RequestBody RequirementUpdateStatusReq req);

    @ApiOperation(value = "删除需求", notes = "按 ID 删除需求，无返回体。")
    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 404, message = "需求不存在", response = ApiErrorResponse.class)
    })
    @DeleteMapping("/{id}")
    void deleteRequirement(@ApiParam(value = "需求 ID", example = "1", required = true) @PathVariable("id") Long id);
}
