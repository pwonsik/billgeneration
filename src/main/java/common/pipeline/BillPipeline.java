package common.pipeline;

import common.domain.BillContext;
import java.util.List;

/**
 * 청구 계산 파이프라인 인터페이스
 * Nova CalculationPipeline 패턴을 참조하여 구현
 */
public interface BillPipeline {

    /**
     * 이 파이프라인이 주어진 컨텍스트를 지원하는지 확인
     * 
     * @param context 청구 컨텍스트
     * @return 지원 여부
     */
    boolean supports(BillContext context);

    /**
     * 파이프라인의 계산 단계들을 반환
     * 
     * @return 계산 단계 리스트
     */
    List<BillStep> getSteps();

    /**
     * 파이프라인을 실행하여 모든 단계를 순차적으로 처리
     * 
     * @param context 청구 컨텍스트
     */
    default void execute(BillContext context) {
        if (!supports(context)) {
            throw new IllegalArgumentException("이 파이프라인은 주어진 컨텍스트를 지원하지 않습니다: " + context);
        }
        
        for (BillStep step : getSteps()) {
            step.execute(context);
        }
    }
}