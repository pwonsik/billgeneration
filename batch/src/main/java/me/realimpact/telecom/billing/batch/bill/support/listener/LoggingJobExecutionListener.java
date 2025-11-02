package me.realimpact.telecom.billing.batch.bill.support.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("================== {} starts.==================", jobExecution.getJobInstance().getJobName());
        
        JobParameters jobParameters = jobExecution.getJobParameters();
        
        if (jobParameters != null) {
            log.info("┌───────────────────────────────────────────┐");
            log.info("│           Job Parameters Start            │");
            log.info("└───────────────────────────────────────────┘");
            jobParameters.getParameters().forEach((key, jobParameter) -> {

                // Key를 20자 길이로 왼쪽 정렬
                String formattedKey = String.format("%-20s", key);
                // Value를 30자 길이로 왼쪽 정렬 (null 값 처리 포함)
                String value = (jobParameter.getValue() != null) ? String.valueOf(jobParameter.getValue()) : "null";
                String formattedValue = String.format("%-10s", value);
                log.info("  Key: {}, Value: {} ({})", formattedKey, formattedValue, jobParameter.getType());            	
            	
            });
            log.info("┌───────────────────────────────────────────┐");
            log.info("│            Job Parameters End             │");
            log.info("└───────────────────────────────────────────┘");
        } else {
            log.info("Job Parameters are null.");
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("{} ends with status: {}.",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus());
    }
}
