package me.realimpact.telecom.billing.batch.calculation.config;

import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DefaultBatchConfiguration.class)
public class BatchInfrastructureConfig {

}
