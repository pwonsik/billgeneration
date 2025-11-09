package common.pipeline;

import java.util.List;

import common.domain.BillContext;

/**
 * ID 리스트를 특정 타입으로 로드하는 인터페이스
 * 
 * @param <ID> 식별자 타입 (계약번호, 계정번호 등)
 * @param <T> 로드할 타겟 타입
 */
@FunctionalInterface
public interface DataLoader<ID, T> {
    
    /**
     * ID 목록을 타겟 타입 목록으로 로드
     * 
     * @param ids ID 목록 (계약번호, 계정번호 등)
     * @param context 청구 컨텍스트
     * @return 로드된 타겟 객체 목록
     */
    List<T> load(List<ID> ids, BillContext context);
    
    /**
     * 로더의 이름 (디버깅용)
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}