package tt.heixiong.awesome.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel(description = "统一错误响应")
public class ApiErrorResponse {

    @ApiModelProperty(value = "错误消息", example = "Validation failed")
    private String message;

    @ApiModelProperty(value = "字段级错误列表", example = "[\"title: must not be blank\"]")
    private List<String> errors;
}
