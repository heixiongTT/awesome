package tt.heixiong.awesome.req;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RequirementUpdateStatusReq {

    @NotNull
    private Long id;

    @NotBlank
    private String status;
}
