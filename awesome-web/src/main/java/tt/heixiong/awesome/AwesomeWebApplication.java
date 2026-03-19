package tt.heixiong.awesome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients(basePackages = "tt.heixiong.awesome.client")
@EnableJpaAuditing
@EnableEurekaClient
public class AwesomeWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwesomeWebApplication.class, args);
    }
}
