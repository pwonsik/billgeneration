package common.pipeline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wirelessbill.domain.CalculationTarget2;
import common.domain.BillContext;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 계약 ID를 {@link CalculationTarget2}로 로드하는 파이프라인 구현체입니다.
 * BillSample1 작업 배치에서 사용됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BillSample1Pipeline implements DataLoader<Long, CalculationTarget2> {

    @Override
    public List<CalculationTarget2> load(List<Long> contractIds, BillContext context) {
        log.debug("계약 ID {}건을 CalculationTarget2로 로드 시작", contractIds.size());
        
        // 실제로는 DB에서 조회하겠지만, 예시로 더미 데이터 생성
        List<CalculationTarget2> result = contractIds.stream()
            .map(contractId -> new CalculationTarget2(
                contractId,
                "BillSample1-Customer-" + contractId,
                BigDecimal.valueOf(15000), // 조금 다른 금액으로 구분
                null, // BillContext에서 날짜 정보 가져올 예정
                "BillSample1-Plan"
            ))
            .collect(Collectors.toList());
        
        log.debug("BillSample1 CalculationTarget2 로드 완료: {}건", result.size());
        return result;
    }
}