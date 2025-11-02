

package me.realimpact.telecom.billing.batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "me.realimpact.telecom.calculation",
        "me.realimpact.telecom.billing.batch",
        "me.realimpact.telecom.billing.batch", // For reader, processor, writer, config
        "me.realimpact.telecom.bill.prework", // For domain, infrastructure, mapper
})
@MapperScan(basePackages = {
        "me.realimpact.telecom.calculation.infrastructure.adapter.mybatis",
        "me.realimpact.telecom.calculation.policy.monthlyfee.adapter.mybatis",
        "me.realimpact.telecom.calculation.policy.onetimecharge.adapter.mybatis",
        "me.realimpact.telecom.bill.prework.infrastructure.mapper", // For InvObjAcntMapper
})
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}

