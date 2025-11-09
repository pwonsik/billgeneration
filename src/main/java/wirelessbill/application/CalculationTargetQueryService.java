package bill.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import bill.domain.CalculationContext;
import bill.domain.CalculationTarget;

import java.util.List;

/**
 * 계산 타겟 조회 전용 서비스
 * Spring Batch Reader에서 사용하는 데이터 조회 전담 서비스
 * 비즈니스 로직과 분리하여 단일 책임 원칙 준수
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CalculationTargetQueryService {
    private final CalculationTargetLoader targetLoader;

    /**
     * 계산 대상 조회
     * @param contractIds 계약 ID 목록
     * @param ctx 계산 컨텍스트
     * @return 계산 대상 목록
     */
    public List<CalculationTarget> loadCalculationTargets(List<Long> contractIds, CalculationContext ctx) {
        log.debug("Loading calculation targets for {} contracts", contractIds.size());
        return targetLoader.load(contractIds, ctx);
    }
}