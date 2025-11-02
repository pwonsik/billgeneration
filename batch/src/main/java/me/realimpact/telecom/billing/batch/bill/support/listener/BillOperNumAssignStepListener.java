package me.realimpact.telecom.billing.batch.bill.support.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BillOperNumAssignStepListener implements StepExecutionListener {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String threadName = Thread.currentThread().getName();
        log.info("[Partition Summary - {}] Updated Records: {}", threadName, stepExecution.getWriteCount());
        return stepExecution.getExitStatus();
    }
}