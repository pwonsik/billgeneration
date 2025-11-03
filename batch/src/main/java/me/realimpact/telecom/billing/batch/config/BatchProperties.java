package me.realimpact.telecom.billing.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "batch")
@Validated
@Data
public class BatchProperties {

    @Max(value = 10, message = "'batch.thread-count'의 값은 10보다 커서는 안됩니다.")
    private Integer threadCount = 8; // 기본값 8로 설정

}
