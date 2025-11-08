

package batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "bill",
        "batch"
})
@MapperScan(basePackages = {
        "bill.infrastructure.adapter.mybatis",
        "bill.policy.monthlyfee.adapter.mybatis",
        "bill.policy.onetimecharge.adapter.mybatis"
})
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}

