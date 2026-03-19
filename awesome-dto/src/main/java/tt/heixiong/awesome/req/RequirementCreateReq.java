package tt.heixiong.awesome.req;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RequirementCreateReq {

    @NotBlank
    private String title;

    private String description;

    private String priority;

    private String creator;

    private String status;
}
