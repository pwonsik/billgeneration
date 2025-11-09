package bill.policy.monthlyfee.domain;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import bill.domain.monthlyfee.AdditionalBillingFactor;
import bill.domain.monthlyfee.Pricing;


/*
 * 단가 * 건수 계산 정책
 */
@RequiredArgsConstructor
public class UnitPriceFactorPolicy implements Pricing {

    private final String factorKey;
    private final BigDecimal unitPrice;

    @Override
    public BigDecimal getPrice(List<AdditionalBillingFactor> additionalBillingFactors) {
        // 건수 조회
        Long count = additionalBillingFactors.stream()
            .map(factor -> factor.getFactorValue(factorKey, Long.class))
            .filter(Optional::isPresent)
            .map(opt -> opt.orElse(0L))
            .findFirst()
            .orElse(0L);

        return unitPrice.multiply(BigDecimal.valueOf(count));
    }
}
