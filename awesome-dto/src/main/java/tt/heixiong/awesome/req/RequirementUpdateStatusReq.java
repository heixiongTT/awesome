package tt.heixiong.awesome.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequirementUpdateStatusReq {

    @NotNull
    private Long id;

    @NotBlank
    private String status;
}
