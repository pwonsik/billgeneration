package batch.common.pipeline;

import java.util.List;

import bill.domain.CalculationContext;

/**
 * 원시 계약 ID 데이터를 특정 타입의 타겟 객체로 변환하는 파이프라인 인터페이스입니다.
 * 제네릭의 복잡성을 피하고 파이프라인 패턴을 사용하여 가독성과 확장성을 개선합니다.
 *
 * @param <T> 변환할 타겟 객체의 타입
 */
public interface DataTransformationPipeline<T> {

    /**
     * 주어진 계약 ID 목록을 특정 타입의 타겟 객체 목록으로 변환합니다.
     *
     * @param contractIds 변환할 계약 ID 목록
     * @param context 계산 컨텍스트 (날짜, 타입 등 변환에 필요한 추가 정보 포함)
     * @return 변환된 타겟 객체 목록
     */
    List<T> transform(List<Long> contractIds, CalculationContext context);

    /**
     * 파이프라인의 이름을 반환합니다. 로깅 및 디버깅 목적으로 사용됩니다.
     * @return 파이프라인의 이름
     */
    default String getPipelineName() {
        return this.getClass().getSimpleName();
    }
}
