package me.realimpact.telecom.billing.batch.bill.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(Ordered.LOWEST_PRECEDENCE) // 모든 Job 실행이 끝난 뒤 마지막에 수행
@RequiredArgsConstructor
@Slf4j
public class BatchShutdownRunner implements ApplicationRunner {

    private final ConfigurableApplicationContext ctx;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ctx.getBeansOfType(ThreadPoolTaskExecutor.class)
              .forEach((name, exec) -> {
                  log.info("Shutting down TaskExecutor: {}", name);
                  exec.shutdown();
              });
        } catch (Exception e) {
            log.warn("TaskExecutor shutdown error", e);
        }

        int code = org.springframework.boot.SpringApplication.exit(ctx); // 배치 ExitCode 반영
        log.info("Exiting with code {}", code);
        System.exit(code);
    }
}
