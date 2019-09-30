package tt.heixiong.awesome.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tt.heixiong.awesome.application.StudentApplication;
import tt.heixiong.awesome.req.StudentReq;
import tt.heixiong.awesome.service.StudentService;

@Controller
@RequestMapping("/validate")
@Slf4j
public class ValidateCtrl {

    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentApplication studentApplication;

    @RequestMapping(value = "student", method = RequestMethod.PUT)
    public String createStudent(@Validated @RequestBody StudentReq req) {
        return "redirect:/results";
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    @ResponseBody
    public String view(StudentReq req) {
//        studentService.createStudent(null);
        log.info("test");
        return "zhangsan";
    }

}
