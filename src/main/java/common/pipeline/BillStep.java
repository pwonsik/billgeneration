package common.pipeline;

import common.domain.BillContext;

/**
 * 청구 계산의 각 단계를 나타내는 인터페이스
 * Nova CalculationStep 패턴을 참조하여 구현
 */
@FunctionalInterface
public interface BillStep {

    /**
     * 계산 단계를 실행합니다
     * 
     * @param context 청구 컨텍스트
     */
    void execute(BillContext context);
}