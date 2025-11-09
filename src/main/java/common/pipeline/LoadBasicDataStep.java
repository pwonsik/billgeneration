package common.pipeline;

import org.springframework.stereotype.Component;
import common.domain.BillContext;

/**
 * 기본 데이터 로딩 단계
 * 청구서 생성을 위한 기본 데이터를 로딩하는 역할
 */
@Component
public class LoadBasicDataStep implements BillStep {
    
    @Override
    public void execute(BillContext context) {
        System.out.println("1단계: 기본 데이터 로딩 - 청구일자: " + context.invDt());
        System.out.println("  고객ID: CUST_001");
        System.out.println("  계약ID: CONTRACT_001");
    }
}