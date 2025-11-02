package me.realimpact.telecom.billing.batch.bill.config.prework;

import static me.realimpact.telecom.billing.batch.constant.BatchConstants.CHUNK_SIZE;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.realimpact.telecom.billing.batch.bill.partitioner.prework.BillOperNumAssignPartitioner;
import me.realimpact.telecom.billing.batch.bill.processor.prework.BillOperNumAssignProcessor;
import me.realimpact.telecom.billing.batch.bill.reader.prework.BillOperNumAssignReader;
import me.realimpact.telecom.billing.batch.bill.support.listener.DateRangeJobExecutionListener;
import me.realimpact.telecom.billing.batch.bill.support.listener.LoggingJobExecutionListener;
import me.realimpact.telecom.billing.batch.bill.support.validator.DefaultJobParametersValidator;
import me.realimpact.telecom.bill.prework.domain.InvObjAcnt;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.name", havingValue = "billOperNumAssignJob")
@Slf4j
public class BillOperNumAssignJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;
    private final BillOperNumAssignProcessor billOperNumAssignProcessor;

    @Bean("billOperNumAssignJob")
    Job billOperNumAssignJob(DefaultJobParametersValidator defaultJobParametersValidator,
                             LoggingJobExecutionListener loggingJobExecutionListener,
                             DateRangeJobExecutionListener dateRangeJobExecutionListener) {
        return new JobBuilder("billOperNumAssignJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .validator(defaultJobParametersValidator)
                .listener(loggingJobExecutionListener)
                .listener(dateRangeJobExecutionListener)
                .start(billOperNumAssignMasterStep())
                .build();
    }

    @Bean("billOperNumAssignTaskExecutor")
    TaskExecutor billOperNumAssignTaskExecutor(@Value("${batch.thread-count:8}") Integer threadCount) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setThreadNamePrefix("billoperassign-partition-");
        executor.initialize();
        return executor;
    }

    @Bean
    @StepScope
    Partitioner billOperNumAssignPartitioner(
            @Value("#{jobParameters['invOperCyclCd']}") final String invOperCyclCd) {
        return new BillOperNumAssignPartitioner(invOperCyclCd);
    }

    @Bean("billOperNumAssignPartitionHandler")
    PartitionHandler billOperNumAssignPartitionHandler(@Value("${batch.thread-count:8}") Integer threadCount) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(billOperNumAssignWorkerStep());
        partitionHandler.setTaskExecutor(billOperNumAssignTaskExecutor(threadCount));
        partitionHandler.setGridSize(threadCount);
        return partitionHandler;
    }

    @Bean("billOperNumAssignMasterStep")
    Step billOperNumAssignMasterStep() {
        return new StepBuilder("billOperNumAssignMasterStep", jobRepository)
                .partitioner("billOperNumAssignWorkerStep", billOperNumAssignPartitioner(null))
                .partitionHandler(billOperNumAssignPartitionHandler(null))
                .build();
    }

    @Bean("billOperNumAssignWorkerStep")
    Step billOperNumAssignWorkerStep() {
        return new StepBuilder("billOperNumAssignWorkerStep", jobRepository)
                .<InvObjAcnt, InvObjAcnt>chunk(CHUNK_SIZE, transactionManager)
                .reader(billOperNumAssignItemReader(null, null, null))
                .processor(billOperNumAssignProcessor)
                .writer(billOperNumAssignItemWriter())
                .build();
    }

    @Bean
    MyBatisBatchItemWriter<InvObjAcnt> billOperNumAssignItemWriter() {
        return new MyBatisBatchItemWriterBuilder<InvObjAcnt>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("me.realimpact.telecom.bill.prework.infrastructure.mapper.InvObjAcntMapper.updateInvObjAcnt")
                .build();
    }

    @Bean
    @StepScope
    MyBatisPagingItemReader<InvObjAcnt> billOperNumAssignItemReader(
            @Value("#{stepExecutionContext['invOperCyclCd']}") String invOperCyclCd,
            @Value("#{stepExecutionContext['partitionKey']}") Integer partitionKey,
            @Value("#{stepExecutionContext['partitionCount']}") Integer partitionCount) {
        return BillOperNumAssignReader.newInstance(sqlSessionFactory, partitionKey, partitionCount, invOperCyclCd);
    }
}
