package sevenstar.marineleisure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import sevenstar.marineleisure.global.api.config.properties.KhoaProperties;
import sevenstar.marineleisure.global.api.config.properties.OpenMeteoProperties;

@SpringBootApplication
@EnableConfigurationProperties({KhoaProperties.class, OpenMeteoProperties.class})
@EnableAsync
@EnableCaching
public class MarineLeisureApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarineLeisureApplication.class, args);
	}

}
