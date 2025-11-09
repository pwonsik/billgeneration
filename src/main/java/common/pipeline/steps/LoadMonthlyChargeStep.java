package common.pipeline.steps;

import java.util.List;
import java.util.Map;

import common.pipeline.CalculationTargetBuilder;
import common.pipeline.DataLoadingStep;
import wirelessbill.application.monthlyfee.MonthlyFeeDataLoader;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.monthlyfee.MonthlyChargeDomain;

/**
 * MonthlyCharge 데이터를 로딩하는 파이프라인 단계
 * 
 * @param <T> MonthlyChargeDomain 하위 타입
 */
public class LoadMonthlyChargeStep<T extends MonthlyChargeDomain> implements DataLoadingStep<CalculationTargetBuilder> {
    
    private final MonthlyFeeDataLoader<T> dataLoader;
    
    public LoadMonthlyChargeStep(MonthlyFeeDataLoader<T> dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
    public void execute(CalculationTargetBuilder builder, Long contractId, CalculationContext context) {
        // 단일 계약 ID로 조회
        Map<Long, List<? extends MonthlyChargeDomain>> resultMap = dataLoader.read(List.of(contractId), context);
        List<? extends MonthlyChargeDomain> data = resultMap.getOrDefault(contractId, List.of());
        
        @SuppressWarnings("unchecked")
        List<T> typedData = (List<T>) data;
        
        builder.addMonthlyChargeData(dataLoader.getDomainType(), typedData);
    }
}
