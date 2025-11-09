package common.pipeline;

import org.springframework.stereotype.Component;
import common.domain.BillContext;

import java.util.List;

/**
 * Nova 스타일 BillSample1 파이프라인
 * 기본 청구서 생성을 위한 단계별 계산 파이프라인
 */
@Component
public class BillSample1Pipeline implements BillPipeline {
    
    private final LoadBasicDataStep loadBasicDataStep;
    
    public BillSample1Pipeline(LoadBasicDataStep loadBasicDataStep) {
        this.loadBasicDataStep = loadBasicDataStep;
    }
    
    @Override
    public boolean supports(BillContext context) {
        return true;
    }
    
    @Override
    public List<BillStep> getSteps() {
        return List.of(
            loadBasicDataStep
        );
    }
    
}