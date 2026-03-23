package tt.heixiong.awesome.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiErrorResponse {

    private String message;

    private List<String> errors;
}
