package tt.heixiong.awesome.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel(description = "创建需求请求")
public class RequirementCreateReq {

    @NotBlank
    @ApiModelProperty(value = "需求标题", required = true, example = "补齐 OpenAPI 文档")
    private String title;

    @ApiModelProperty(value = "需求描述", example = "为 REST API 输出 Swagger 文档")
    private String description;

    @ApiModelProperty(value = "优先级", example = "HIGH")
    private String priority;

    @ApiModelProperty(value = "创建人", example = "codex")
    private String creator;

    @ApiModelProperty(value = "需求状态", example = "TODO", notes = "未传时服务端默认回填 TODO")
    private String status;
}
