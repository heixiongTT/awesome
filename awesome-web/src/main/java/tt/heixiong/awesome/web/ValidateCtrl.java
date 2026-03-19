package tt.heixiong.awesome.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import tt.heixiong.awesome.contract.ValidateContract;
import tt.heixiong.awesome.req.StudentReq;
import tt.heixiong.awesome.service.StudentService;

@Controller
@Slf4j
public class ValidateCtrl implements ValidateContract {

    @Autowired
    private StudentService studentService;

    @Override
    public String createStudent(@Validated @RequestBody StudentReq req) {
        return "redirect:/results";
    }

    @Override
    public String view(StudentReq req) {
//        studentService.createStudent(null);
        log.info("test");
        return "zhangsan";
    }
}
