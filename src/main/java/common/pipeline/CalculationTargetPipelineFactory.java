package common.pipeline;

import java.util.List;

import org.springframework.stereotype.Component;

import common.pipeline.steps.LoadDiscountsStep;
import common.pipeline.steps.LoadMonthlyChargeStep;
import common.pipeline.steps.LoadOneTimeChargeStep;
import wirelessbill.application.monthlyfee.MonthlyFeeDataLoader;
import wirelessbill.application.onetimecharge.OneTimeChargeDataLoader;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationTarget;
import wirelessbill.domain.monthlyfee.MonthlyChargeDomain;
import wirelessbill.domain.onetimecharge.OneTimeChargeDomain;
import wirelessbill.port.out.ContractDiscountQueryPort;

/**
 * CalculationTarget 생성 파이프라인 팩토리
 * 
 * 사용 예:
 * <pre>
 * DataTransformationPipeline pipeline = factory.createPipeline(
 *     List.of(monthlyFeeLoader1, monthlyFeeLoader2),
 *     List.of(oneTimeChargeLoader1, oneTimeChargeLoader2)
 * );
 * 
 * List<CalculationTarget> targets = pipeline.transform(contractIds, context);
 * </pre>
 */
@Component
public class CalculationTargetPipelineFactory {
    
    private final ContractDiscountQueryPort discountQueryPort;
    
    public CalculationTargetPipelineFactory(ContractDiscountQueryPort discountQueryPort) {
        this.discountQueryPort = discountQueryPort;
    }
    
    /**
     * CalculationTarget 생성 파이프라인을 생성합니다.
     * 
     * @param monthlyFeeLoaders MonthlyFee 데이터 로더 목록 (순서대로 실행됨)
     * @param oneTimeChargeLoaders OneTimeCharge 데이터 로더 목록 (순서대로 실행됨)
     * @return 설정된 파이프라인
     */
    public DataTransformationPipeline createPipeline(
            List<MonthlyFeeDataLoader<? extends MonthlyChargeDomain>> monthlyFeeLoaders,
            List<OneTimeChargeDataLoader<? extends OneTimeChargeDomain>> oneTimeChargeLoaders) {
        
        DataTransformationPipeline pipeline = new DataTransformationPipeline("CalculationTargetPipeline");
        
        // 1단계: MonthlyCharge 데이터 로딩 (순서대로)
        for (MonthlyFeeDataLoader<? extends MonthlyChargeDomain> loader : monthlyFeeLoaders) {
            pipeline.addStep(new LoadMonthlyChargeStep<>(loader));
        }
        
        // 2단계: OneTimeCharge 데이터 로딩 (순서대로)
        for (OneTimeChargeDataLoader<? extends OneTimeChargeDomain> loader : oneTimeChargeLoaders) {
            pipeline.addStep(new LoadOneTimeChargeStep<>(loader));
        }
        
        // 3단계: Discount 데이터 로딩
        pipeline.addStep(new LoadDiscountsStep(discountQueryPort));
        
        return pipeline;
    }
    
    /**
     * 계약 ID 리스트를 CalculationTarget 리스트로 변환
     * 
     * @param contractIds 계약 ID 목록
     * @param context 계산 컨텍스트
     * @param monthlyFeeLoaders MonthlyFee 로더들
     * @param oneTimeChargeLoaders OneTimeCharge 로더들
     * @return CalculationTarget 목록
     */
    public List<CalculationTarget> transform(
            List<Long> contractIds,
            CalculationContext context,
            List<MonthlyFeeDataLoader<? extends MonthlyChargeDomain>> monthlyFeeLoaders,
            List<OneTimeChargeDataLoader<? extends OneTimeChargeDomain>> oneTimeChargeLoaders) {
        
        DataTransformationPipeline pipeline = createPipeline(monthlyFeeLoaders, oneTimeChargeLoaders);
        return pipeline.transform(contractIds, context);
    }
}
