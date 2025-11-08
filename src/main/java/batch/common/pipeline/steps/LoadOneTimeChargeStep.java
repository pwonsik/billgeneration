package batch.common.pipeline.steps;

import java.util.List;
import java.util.Map;

import batch.common.pipeline.CalculationTargetBuilder;
import batch.common.pipeline.DataLoadingStep;
import bill.application.onetimecharge.OneTimeChargeDataLoader;
import bill.domain.CalculationContext;
import bill.domain.onetimecharge.OneTimeChargeDomain;

/**
 * OneTimeCharge 데이터를 로딩하는 파이프라인 단계
 * 
 * @param <T> OneTimeChargeDomain 하위 타입
 */
public class LoadOneTimeChargeStep<T extends OneTimeChargeDomain> implements DataLoadingStep<CalculationTargetBuilder> {
    
    private final OneTimeChargeDataLoader<T> dataLoader;
    
    public LoadOneTimeChargeStep(OneTimeChargeDataLoader<T> dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
    public void execute(CalculationTargetBuilder builder, Long contractId, CalculationContext context) {
        // 단일 계약 ID로 조회
        Map<Long, List<? extends OneTimeChargeDomain>> resultMap = dataLoader.read(List.of(contractId), context);
        List<? extends OneTimeChargeDomain> data = resultMap.getOrDefault(contractId, List.of());
        
        @SuppressWarnings("unchecked")
        List<T> typedData = (List<T>) data;
        
        builder.addOneTimeChargeData(dataLoader.getDomainType(), typedData);
    }
}
