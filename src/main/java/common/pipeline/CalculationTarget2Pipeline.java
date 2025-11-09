package common.pipeline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.CalculationTarget2;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 계약 ID를 {@link CalculationTarget2}로 변환하는 파이프라인 구현체입니다.
 * 2번 작업 배치에서 사용됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalculationTarget2Pipeline implements DataTransformer<CalculationTarget2> {

    @Override
    public List<CalculationTarget2> transform(List<Long> contractIds, CalculationContext context) {
        log.debug("계약 ID {}건을 CalculationTarget2로 변환 시작", contractIds.size());
        
        // 실제로는 DB에서 조회하겠지만, 예시로 더미 데이터 생성
        List<CalculationTarget2> result = contractIds.stream()
            .map(contractId -> new CalculationTarget2(
                contractId,
                "Customer-" + contractId,
                BigDecimal.valueOf(10000),
                context.billingStartDate(),
                "Premium"
            ))
            .collect(Collectors.toList());
        
        log.debug("CalculationTarget2 변환 완료: {}건", result.size());
        return result;
    }
}