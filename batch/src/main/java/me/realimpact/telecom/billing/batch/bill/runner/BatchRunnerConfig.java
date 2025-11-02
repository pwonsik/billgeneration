package me.realimpact.telecom.billing.batch.bill.runner;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchRunnerConfig {

//    @Bean
//    JobLauncherApplicationRunner jobLauncherApplicationRunner(
//            JobLauncher jobLauncher,
//            JobExplorer jobExplorer,
//            JobRepository jobRepository,
//            ObjectProvider<JobRegistry> jobRegistryProvider
//    ) {
//        var runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
//        runner.setJobParametersConverter(new DefaultJobParametersConverter()); // 표준 컨버터
//        jobRegistryProvider.ifAvailable(runner::setJobRegistry);
//        return runner;
//    }
    
    @Bean
    JobLauncherApplicationRunner jobLauncherApplicationRunner(
        JobLauncher jobLauncher, JobExplorer jobExplorer,
        JobRepository jobRepository, ObjectProvider<JobRegistry> reg) {

      var runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
      // runner.setJobParametersConverter(...)  // ← 이 줄 없음!
      reg.ifAvailable(runner::setJobRegistry);
      return runner;
    }    
}

