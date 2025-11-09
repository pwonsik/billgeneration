package batch.common.pipeline;

import wirelessbill.domain.CalculationContext;

/**
 * 파이프라인의 각 단계를 나타내는 인터페이스
 * 각 Step은 CalculationTargetBuilder를 받아서 데이터를 추가하고 다음 단계로 전달합니다.
 *
 * @param <T> 빌더 타입 (CalculationTargetBuilder)
 */
@FunctionalInterface
public interface DataLoadingStep<T> {
    
    /**
     * 데이터 로딩 단계를 실행합니다.
     * 
     * @param builder 계산 타겟 빌더
     * @param contractId 계약 ID
     * @param context 계산 컨텍스트
     */
    void execute(T builder, Long contractId, CalculationContext context);
}
