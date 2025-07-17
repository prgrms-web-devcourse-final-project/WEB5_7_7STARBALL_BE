package sevenstar.marineleisure.global.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	@Value("${kakao.login.api_key}")
	private String kakaoRestApiKey;

	@Bean
	public RestTemplate apiRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public RestTemplate kakaoRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
			request.getHeaders().add("Authorization", String.format("KakaoAK %s", kakaoRestApiKey));
			return execution.execute(request, body);
		};

		restTemplate.getInterceptors().add(interceptor);
		return restTemplate;
	}
}
