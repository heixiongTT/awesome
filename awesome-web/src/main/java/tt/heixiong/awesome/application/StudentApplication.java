package tt.heixiong.awesome.application;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${spring.application.name}")
public interface StudentApplication {

    @GetMapping(value = "/student")
    String getStudentByRequest(String name);

    @GetMapping(value = "/name")
    String getStudentByName(String name);
}
