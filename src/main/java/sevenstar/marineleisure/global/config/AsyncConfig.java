package sevenstar.marineleisure.global.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 기본 스레드 수
        executor.setMaxPoolSize(4);  // 최대 스레드 수
        executor.setQueueCapacity(100); // 큐 용량
        executor.setThreadNamePrefix("Async-Task-With-Sevenball-");
        executor.initialize();
        return executor;
    }
}
