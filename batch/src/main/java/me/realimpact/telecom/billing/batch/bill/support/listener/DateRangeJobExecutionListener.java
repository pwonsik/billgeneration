package me.realimpact.telecom.billing.batch.bill.support.listener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DateRangeJobExecutionListener implements JobExecutionListener {

    private static final DateTimeFormatter YYYYMMDD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void beforeJob(JobExecution jobExecution) {
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();

        // invDt 파라미터 처리
        String invDt = jobExecution.getJobParameters().getString("invDt");

        // 청구일자가 존재하지 않으면 전월 말일로 기본값 설정
        if (!StringUtils.hasText(invDt)) {
            invDt = LocalDate.now()
            		.minusMonths(1)
            		.with(TemporalAdjusters.lastDayOfMonth())
            		.format(YYYYMMDD_FORMATTER);
        }

        jobExecutionContext.put("invDt", invDt);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Job 완료 후 처리가 필요하면 여기에 구현
    }
}