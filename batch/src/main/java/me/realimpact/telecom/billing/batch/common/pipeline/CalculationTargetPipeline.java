package me.realimpact.telecom.billing.batch.common.pipeline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.realimpact.telecom.calculation.application.CalculationTargetQueryService;
import me.realimpact.telecom.calculation.domain.CalculationContext;
import me.realimpact.telecom.calculation.domain.CalculationTarget;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 계약 ID를 {@link CalculationTarget}으로 변환하는 파이프라인 구현체입니다.
 * 월정액 계산 배치 작업에서 사용됩니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CalculationTargetPipeline implements DataTransformationPipeline<CalculationTarget> {

    private final CalculationTargetQueryService calculationTargetQueryService;

    @Override
    public List<CalculationTarget> transform(List<Long> contractIds, CalculationContext context) {
        log.debug("계약 ID {}건을 CalculationTarget으로 변환 시작", contractIds.size());
        List<CalculationTarget> result = calculationTargetQueryService.loadCalculationTargets(contractIds, context);
        log.debug("CalculationTarget 변환 완료: {}건", result.size());
        return result;
    }
}
