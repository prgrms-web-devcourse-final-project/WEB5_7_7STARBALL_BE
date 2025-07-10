package sevenstar.marineleisure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
// @EnableJpaAuditing
public class MarineLeisureApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarineLeisureApplication.class, args);
	}

}
