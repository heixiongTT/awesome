package tt.heixiong.awesome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableEurekaClient
@EnableHystrix
public class AwesomeWebApplication {

    public static void main(String[] args) throws Exception {
/*        Student student = new Student();
        student.setId(1L);
        student.setName("lily");
        student.setAge(18);
        ObjectMapper om = new ObjectMapper();
        try {
            System.out.println(om.writeValueAsString(student));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/
        SpringApplication.run(AwesomeWebApplication.class, args);

    }

}
