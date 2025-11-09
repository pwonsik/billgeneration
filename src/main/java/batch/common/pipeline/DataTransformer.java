package batch.common.pipeline;

import java.util.List;

import wirelessbill.domain.CalculationContext;

/**
 * 계약 ID 리스트를 특정 타입으로 변환하는 인터페이스 (레거시 호환용)
 * 
 * @param <T> 변환할 타겟 타입
 */
@FunctionalInterface
public interface DataTransformer<T> {
    
    /**
     * 계약 ID 목록을 타겟 타입 목록으로 변환
     * 
     * @param contractIds 계약 ID 목록
     * @param context 계산 컨텍스트
     * @return 변환된 타겟 객체 목록
     */
 
      List<T> transform(List<Long> contractIds, CalculationContext context); 
    /**
     * 변환기의 이름 (디버깅용)
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
