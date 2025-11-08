package batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import batch.BillParameters;
import batch.common.pipeline.CalculationTarget2Pipeline;
import batch.common.reader.UniversalChunkedReader;
import batch.common.reader.UniversalPartitionedReader;
import bill.domain.BillingCalculationPeriod;
import bill.domain.BillingCalculationType;
import bill.domain.CalculationTarget2;

import static batch.common.constant.BatchConstants.CHUNK_SIZE;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.name", havingValue = "sample1Job")
@Slf4j
public class BillSample1Config {

    private final SqlSessionFactory sqlSessionFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    // CalculationTarget2용 파이프라인 주입
    private final CalculationTarget2Pipeline calculationTarget2Pipeline;

    /**
     * 2번 작업 Job
     */
    @Bean
    public Job secondCalculationJob() {
        return new JobBuilder("secondCalculationJob", jobRepository)
                .start(secondCalculationStep())
                .build();
    }

    /**
     * 2번 작업 Step
     */
    @Bean
    public Step secondCalculationStep() {
        return new StepBuilder("secondCalculationStep", jobRepository)
                .<CalculationTarget2, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(secondCalculationReader(null, null, null, null, null, null))
                .processor(secondCalculationProcessor())
                .writer(secondCalculationWriter())
                .build();
    }

    /**
     * CalculationTarget2를 읽는 청크 리더
     * - UniversalChunkedReader<CalculationTarget2>로 명확하게 타입 지정
     * - calculationTarget2Pipeline으로 Contract ID → CalculationTarget2 변환
     */
    @Bean
    @StepScope
    ItemStreamReader<CalculationTarget2> secondCalculationReader(
            @Value("${billingStartDate}") String billingStartDateStr,
            @Value("${billingEndDate}") String billingEndDateStr,
            @Value("${contractIds:}") String contractIdsStr,
            @Value("${batch.thread-count}") Integer threadCount,
            @Value("${billingCalculationType}") String billingCalculationTypeStr,
            @Value("${billingCalculationPeriod}") String billingCalculationPeriodStr
    ) {
        log.info("=== SecondCalculationReader Bean 생성 시작 (CalculationTarget2 사용) ===");

        BillParameters params = createCalculationParameters(
                billingStartDateStr, billingEndDateStr, contractIdsStr,
                threadCount, billingCalculationTypeStr, billingCalculationPeriodStr
        );

        // 범용 청크 리더 생성 - CalculationTarget2 타입으로 변환하는 파이프라인 사용
        UniversalChunkedReader<CalculationTarget2> reader = new UniversalChunkedReader<>(
                calculationTarget2Pipeline,  // Contract ID → CalculationTarget2 변환 (1번과 다른 파이프라인!)
                sqlSessionFactory,
                params
        );

        log.info("=== UniversalChunkedReader<CalculationTarget2> Bean 생성 완료 ===");
        return reader;
    }

    /**
     * 파티션 리더 예시 - CalculationTarget2 사용
     */
    @Bean
    @StepScope
    ItemStreamReader<CalculationTarget2> secondPartitionedReader(
            @Value("${billingStartDate}") String billingStartDateStr,
            @Value("${billingEndDate}") String billingEndDateStr,
            @Value("${contractIds:}") String contractIdsStr,
            @Value("${batch.thread-count}") Integer threadCount,
            @Value("${billingCalculationType}") String billingCalculationTypeStr,
            @Value("${billingCalculationPeriod}") String billingCalculationPeriodStr,
            @Value("#{stepExecutionContext['partitionKey']}") Integer partitionKey,
            @Value("#{stepExecutionContext['partitionCount']}") Integer partitionCount
    ) {
        log.info("=== SecondPartitionedReader Bean 생성 시작 (CalculationTarget2 사용) ===");

        BillParameters params = createCalculationParameters(
                billingStartDateStr, billingEndDateStr, contractIdsStr,
                threadCount, billingCalculationTypeStr, billingCalculationPeriodStr
        );

        // 범용 파티션 리더 생성 - CalculationTarget2 타입으로 변환하는 파이프라인 사용
        UniversalPartitionedReader<CalculationTarget2> reader = new UniversalPartitionedReader<>(
                calculationTarget2Pipeline,  // Contract ID → CalculationTarget2 변환 (1번과 다른 파이프라인!)
                sqlSessionFactory,
                params,
                partitionKey,
                partitionCount
        );

        log.info("=== UniversalPartitionedReader<CalculationTarget2> Bean 생성 완료 ===");
        return reader;
    }

    /**
     * CalculationTarget2를 처리하는 프로세서 (예시)
     */
    @Bean
    public ItemProcessor<CalculationTarget2, String> secondCalculationProcessor() {
        return item -> {
            log.debug("Processing CalculationTarget2: contractId={}, customer={}, amount={}",
                    item.contractId(), item.customerName(), item.baseAmount());
            
            // 실제 처리 로직
            return "Processed: " + item.contractId() + " - " + item.customerName();
        };
    }

    /**
     * 결과를 저장하는 Writer (예시)
     */
    @Bean
    public ItemWriter<String> secondCalculationWriter() {
        return items -> {
            for (String item : items) {
                log.info("Writing result: {}", item);
            }
        };
    }

    /**
     * Helper method to create CalculationParameters
     */
    private BillParameters createCalculationParameters(
            String billingStartDateStr,
            String billingEndDateStr,
            String contractIdsStr,
            Integer threadCount,
            String billingCalculationTypeStr,
            String billingCalculationPeriodStr
    ) {
        LocalDate billingStartDate = LocalDate.parse(billingStartDateStr);
        LocalDate billingEndDate = LocalDate.parse(billingEndDateStr);

        List<Long> contractIds = (contractIdsStr == null || contractIdsStr.isBlank())
                ? List.of()
                : Arrays.stream(contractIdsStr.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();

        BillingCalculationType billingCalculationType = BillingCalculationType.valueOf(billingCalculationTypeStr);
        BillingCalculationPeriod billingCalculationPeriod = BillingCalculationPeriod.valueOf(billingCalculationPeriodStr);

        return new BillParameters(
                billingStartDate,
                billingEndDate,
                billingCalculationType,
                billingCalculationPeriod,
                threadCount,
                contractIds
        );
    }
}
