package tt.heixiong.awesome.req;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class StudentReq {

    private String id;

    @NotNull
    private String name;

    private Integer age;
}
