package tt.heixiong.awesome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AwesomeWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwesomeWebApplication.class, args);
    }
}
