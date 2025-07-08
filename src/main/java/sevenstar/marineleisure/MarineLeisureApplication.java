package sevenstar.marineleisure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import sevenstar.marineleisure.global.api.config.properties.KhoaProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(KhoaProperties.class)
public class MarineLeisureApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarineLeisureApplication.class, args);
	}

}
