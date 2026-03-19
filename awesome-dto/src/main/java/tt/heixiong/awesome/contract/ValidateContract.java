package tt.heixiong.awesome.contract;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tt.heixiong.awesome.req.StudentReq;

public interface ValidateContract {

    @PutMapping("/student")
    String createStudent(@Validated @RequestBody StudentReq req);

    @GetMapping("/view")
    @ResponseBody
    String view(StudentReq req);
}
