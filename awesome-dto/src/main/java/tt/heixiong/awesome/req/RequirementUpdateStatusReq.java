package tt.heixiong.awesome.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(description = "更新需求状态请求")
public class RequirementUpdateStatusReq {

    @NotNull
    @ApiModelProperty(value = "需求 ID", required = true, example = "1")
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "新状态", required = true, example = "DONE")
    private String status;
}
