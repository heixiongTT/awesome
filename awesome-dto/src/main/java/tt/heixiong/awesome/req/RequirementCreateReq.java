package tt.heixiong.awesome.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

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
