package tt.heixiong.awesome.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.heixiong.awesome.domain.Student;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Service
public class StudentServiceImpl implements StudentService{


    @Autowired
    private Validator validator;

    @Override
    @HystrixCommand(fallbackMethod = "test")
    public Student createStudent(Student student) {
        Set<ConstraintViolation<Student>> set = validator.validate(student);
        StringBuffer errors = new StringBuffer();
        if(set.size() > 0) {
            set.forEach(e -> errors.append(e.getMessage()).append(";"));
        }
        return null;
    }
}
