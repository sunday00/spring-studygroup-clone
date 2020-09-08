package lec.spring.studygroupclone.config;

import lec.spring.studygroupclone.helpers.ConsoleLog;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int processor = Runtime.getRuntime().availableProcessors();
        ConsoleLog.print("CPU CORE" + processor);
        executor.setCorePoolSize(processor);
        executor.setMaxPoolSize(processor * 2);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(120);
        executor.setThreadNamePrefix("ASYNC-EXECUTOR-");
        executor.initialize();

        return executor;
    }
}
