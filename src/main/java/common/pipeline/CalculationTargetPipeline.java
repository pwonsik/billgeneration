package common.pipeline;

import java.util.List;

import org.springframework.stereotype.Component;

import common.pipeline.steps.LoadDiscountsStep;
import common.pipeline.steps.LoadMonthlyChargeStep;
import common.pipeline.steps.LoadOneTimeChargeStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wirelessbill.application.monthlyfee.MonthlyFeeDataLoader;
import wirelessbill.application.onetimecharge.OneTimeChargeDataLoader;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationTarget;
import wirelessbill.domain.monthlyfee.MonthlyChargeDomain;
import wirelessbill.domain.onetimecharge.OneTimeChargeDomain;
import wirelessbill.port.out.ContractDiscountQueryPort;

/**
 * 계약 ID를 {@link CalculationTarget}으로 변환하는 파이프라인 구현체입니다.
 * 여러 데이터 로딩 단계를 순차적으로 실행하여 CalculationTarget을 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalculationTargetPipeline implements DataTransformer<CalculationTarget> {

    private final List<MonthlyFeeDataLoader<? extends MonthlyChargeDomain>> monthlyFeeLoaders;
    private final List<OneTimeChargeDataLoader<? extends OneTimeChargeDomain>> oneTimeChargeLoaders;
    private final ContractDiscountQueryPort discountQueryPort;

    @Override
    public List<CalculationTarget> transform(List<Long> contractIds, CalculationContext context) {
        log.debug("계약 ID {}건을 CalculationTarget으로 변환 시작", contractIds.size());
        
        // 파이프라인 구성
        DataTransformationPipeline pipeline = new DataTransformationPipeline("CalculationTargetPipeline");
        
        // MonthlyCharge 로딩 단계 추가
        for (MonthlyFeeDataLoader<? extends MonthlyChargeDomain> loader : monthlyFeeLoaders) {
            pipeline.addStep(new LoadMonthlyChargeStep<>(loader));
        }
        
        // OneTimeCharge 로딩 단계 추가
        for (OneTimeChargeDataLoader<? extends OneTimeChargeDomain> loader : oneTimeChargeLoaders) {
            pipeline.addStep(new LoadOneTimeChargeStep<>(loader));
        }
        
        // Discount 로딩 단계 추가
        pipeline.addStep(new LoadDiscountsStep(discountQueryPort));
        
        // 파이프라인 실행
        List<CalculationTarget> result = pipeline.transform(contractIds, context);
        
        log.debug("CalculationTarget 변환 완료: {}건", result.size());
        return result;
    }
}