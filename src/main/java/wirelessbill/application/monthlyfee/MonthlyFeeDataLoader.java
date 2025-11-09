package wirelessbill.application.monthlyfee;

import java.util.List;
import java.util.Map;

import wirelessbill.domain.CalculationContext;
import wirelessbill.domain.monthlyfee.MonthlyChargeDomain;

/**
 * Monthly Fee 데이터 로딩 인터페이스
 * 특정 Monthly Fee 도메인 타입의 데이터 로딩 로직을 정의
 *
 * @param <T> MonthlyChargeDomain을 구현한 Monthly Fee 도메인 타입
 */
public interface MonthlyFeeDataLoader<T extends MonthlyChargeDomain> {

    /**
     * 로딩할 수 있는 데이터 타입 반환
     * @return 데이터 타입
     */
    Class<T> getDomainType();

    /**
     * 계약 ID 목록에 대한 데이터 로딩
     * @param contractIds 계약 ID 목록
     * @param context 계산 컨텍스트
     * @return 로딩된 데이터 목록 (계약 ID별로 그룹화)
     */
    Map<Long, List<? extends MonthlyChargeDomain>> read(List<Long> contractIds, CalculationContext context);
}