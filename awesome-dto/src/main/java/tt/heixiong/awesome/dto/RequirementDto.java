package tt.heixiong.awesome.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel(description = "需求响应")
public class RequirementDto {

    @ApiModelProperty(value = "需求 ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "需求标题", example = "补齐 OpenAPI 文档")
    private String title;

    @ApiModelProperty(value = "需求描述", example = "补充 schema、错误码与示例")
    private String description;

    @ApiModelProperty(value = "优先级", example = "HIGH")
    private String priority;

    @ApiModelProperty(value = "需求状态", example = "TODO")
    private String status;

    @ApiModelProperty(value = "创建人", example = "codex")
    private String creator;

    @ApiModelProperty(value = "创建时间", example = "2026-03-19T06:20:00")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间", example = "2026-03-19T06:20:00")
    private LocalDateTime updatedAt;
}
