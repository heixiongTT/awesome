package tt.heixiong.awesome.web;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tt.heixiong.awesome.dto.StudentDto;
import tt.heixiong.awesome.req.StudentReq;


@RestController
@RequestMapping("/validate")
public class ValidateCtrl {

    @RequestMapping(value = "student", method = RequestMethod.PUT)
    public StudentDto createStudent(@Validated @RequestBody StudentReq req) {
        return null;
    }
}
