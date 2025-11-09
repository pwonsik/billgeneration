package wirelessbill.policy.monthlyfee.domain;

import java.math.BigDecimal;
import java.util.List;

import wirelessbill.domain.monthlyfee.AdditionalBillingFactor;
import wirelessbill.domain.monthlyfee.Pricing;


/* 상품에 정의된 월정액료를 가져와서 일할계산합니다. 가장 기본적인 계산방식입니다. */
public class FlatRatePolicy implements Pricing {
    private final BigDecimal fee;

    public FlatRatePolicy(BigDecimal fee) {
        this.fee = fee;
    }

    @Override
    public BigDecimal getPrice(List<AdditionalBillingFactor> additionalBillingFactors) {
        return this.fee;
    }
}
