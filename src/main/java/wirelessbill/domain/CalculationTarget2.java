package bill.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 2번 작업용 계산 타겟 (예시)
 * 1번 작업과는 다른 데이터 구조
 */
public record CalculationTarget2(
    Long contractId,
    String customerName,
    BigDecimal baseAmount,
    LocalDate calculationDate,
    String productType
) {
}
