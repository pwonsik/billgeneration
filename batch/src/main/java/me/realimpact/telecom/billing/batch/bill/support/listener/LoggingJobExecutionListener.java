package me.realimpact.telecom.billing.batch.bill.support.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingJobExecutionListener implements JobExecutionListener {

    private final Environment env;

    @Autowired
    public LoggingJobExecutionListener(Environment env) {
        this.env = env;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("================== {} starts.==================", jobExecution.getJobInstance().getJobName());
        
        log.info("┌───────────────────────────────────────────┐");
        log.info("│           Job Parameters Start            │");
        log.info("└───────────────────────────────────────────┘");

        JobParameters jobParameters = jobExecution.getJobParameters();
        boolean hasParams = false;
        if (jobParameters != null && !jobParameters.isEmpty()) {
            hasParams = true;
            jobParameters.getParameters().forEach((key, jobParameter) -> {
                String formattedKey = String.format("%-20s", key);
                String value = (jobParameter.getValue() != null) ? String.valueOf(jobParameter.getValue()) : "null";
                String formattedValue = String.format("%-10s", value);
                log.info("  Key: {}, Value: {} ({})", formattedKey, formattedValue, jobParameter.getType());
            });
        }

        String threadCount = env.getProperty("batch.thread-count");
        if (threadCount != null) {
            hasParams = true;
            String formattedKey = String.format("%-20s", "batch.thread-count");
            String formattedValue = String.format("%-10s", threadCount);
            log.info("  Key: {}, Value: {} (String)", formattedKey, formattedValue);
        }
        
        if (!hasParams) {
            log.info("  No Job Parameters found.");
        }

        log.info("┌───────────────────────────────────────────┐");
        log.info("│            Job Parameters End             │");
        log.info("└───────────────────────────────────────────┘");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("{} ends with status: {}.",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus());
    }
}
