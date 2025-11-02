package me.realimpact.telecom.billing.batch.bill.support.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class TotalBillOperNumAssignListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        // This listener should only run for the 'billOperNumAssignJob'
        if (!"billOperNumAssignJob".equals(jobExecution.getJobInstance().getJobName())) {
            return;
        }

        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();

        long totalReadCount = 0;
        long totalWriteCount = 0; // Success
        long totalReadSkipCount = 0;
        long totalProcessSkipCount = 0;
        long totalWriteSkipCount = 0;


        for (StepExecution stepExecution : stepExecutions) {
            // Aggregate results from all partitions of the 'billOperNumAssignWorkerStep'
            if (stepExecution.getStepName().startsWith("billOperNumAssignWorkerStep")) {
                totalReadCount += stepExecution.getReadCount();
                totalWriteCount += stepExecution.getWriteCount();
                totalReadSkipCount += stepExecution.getReadSkipCount();
                totalProcessSkipCount += stepExecution.getProcessSkipCount();
                totalWriteSkipCount += stepExecution.getWriteSkipCount();
            }
        }

        long totalFailureCount = totalReadSkipCount + totalProcessSkipCount + totalWriteSkipCount;

        log.info("");
        log.info("┌───────────────────────────────────────────┐");
        log.info("│    [billOperNumAssignJob] Total Summary   │");
        log.info("├───────────────────────────────────────────┤");
        log.info(String.format("│ Total Target Items : %-21d│", totalReadCount));
        log.info(String.format("│ - Success Count    : %-21d│", totalWriteCount));
        log.info(String.format("│ - Failure Count    : %-21d│", totalFailureCount));
        if (totalFailureCount > 0) {
            log.info("│                                           │");
            log.info("│    Skip Details:                          │");
            log.info(String.format("│      Read-Skip   : %-21d│", totalReadSkipCount));
            log.info(String.format("│      Process-Skip: %-21d│", totalProcessSkipCount));
            log.info(String.format("│      Write-Skip  : %-21d│", totalWriteSkipCount));
        }
        log.info("└───────────────────────────────────────────┘");
    }
}
