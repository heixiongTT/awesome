package tt.heixiong.awesome.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentApplicationImpl implements StudentApplication {

    @Autowired
    private StudentApplication studentApplication;

    @Override
    public String getStudentByRequest(String name) {
        return studentApplication.getStudentByName(name);
    }

    @Override
    public String getStudentByName(String name) {
        return "hello" + name;
    }
}
