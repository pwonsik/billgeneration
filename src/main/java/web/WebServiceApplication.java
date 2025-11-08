
package web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"bill", "web"})
@MapperScan(basePackages = {
        "bill.infrastructure.adapter.mybatis",
        "bill.policy.monthlyfee.adapter.mybatis",
        "bill.policy.onetimecharge.adapter.mybatis"
})
public class WebServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebServiceApplication.class, args);
    }

}


