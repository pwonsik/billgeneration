package me.realimpact.telecom.billing.batch.bill.support.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultJobParametersValidator implements JobParametersValidator {

    // YYYYMMDD 형식 검증을 위한 포맷터
    private static final DateTimeFormatter YYYYMMDD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {

    	// invDt 파라미터 검증
        String invDt = parameters.getString("invDt");
        if (StringUtils.hasText(invDt)) { // invDt 파라미터가 존재할 경우에만 검증
            if (invDt.length() != 8) {
                throw new JobParametersInvalidException("'invDt'는 8자리(yyyyMMdd)여야 합니다.");
            }
            try {
                // yyyyMMdd 형식 및 유효한 날짜인지 검증
                LocalDate.parse(invDt, YYYYMMDD_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new JobParametersInvalidException("'invDt'가 유효한 날짜 형식이 아닙니다: " + invDt);
            }
        }
        
        String threadCount = parameters.getString("batch.thread-count");
        if (StringUtils.hasText(threadCount)) {
            try {
                long count = Long.parseLong(threadCount);
                if (count > 10) {
                    throw new JobParametersInvalidException("'batch.thread-count'의 값은 10보다 커서는 안됩니다.");
                }
            } catch (NumberFormatException e) {
                throw new JobParametersInvalidException("'batch.thread-count'는 숫자여야 합니다.");
            }
        }
    }
}