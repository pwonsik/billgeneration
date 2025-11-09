package wirelessbill.policy.monthlyfee.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wirelessbill.domain.monthlyfee.AdditionalBillingFactor;
import wirelessbill.domain.monthlyfee.ChargeItem;
import wirelessbill.domain.monthlyfee.Product;
import wirelessbill.domain.monthlyfee.ProductOffering;
import wirelessbill.domain.monthlyfee.Suspension;
import wirelessbill.domain.monthlyfee.Temporal;
import wirelessbill.domain.monthlyfee.Suspension.SuspensionType;

@RequiredArgsConstructor
@Builder
@Getter
public class ProratedPeriod extends Temporal {
    private final Temporal period;
    private final ContractWithProductsAndSuspensions contractWithProductsAndSuspensions;
    private final Product product;
    private final ProductOffering productOffering;
    private final ChargeItem chargeItem;
    private final Optional<Suspension> suspension;
    private final List<AdditionalBillingFactor> additionalBillingFactors;

    public long getDayOfMonth() {
        return period.getStartDate().lengthOfMonth();
    }

    private BigDecimal calculateSuspensionRatio() {
        return suspension
            .map(s -> s.getSuspensionType() == SuspensionType.TEMPORARY_SUSPENSION
                ? chargeItem.getSuspensionChargeRatio() 
                : BigDecimal.ZERO)
            .orElse(BigDecimal.ONE);
    }

    @Override
    public LocalDate getStartDate() {
        return period.getStartDate();
    }

    @Override
    public LocalDate getEndDate() {
        return period.getEndDate();
    }

    /**
     * 순수 계산 데이터 생성 (Application Layer에서 CalculationResult 생성에 사용)
     */
    public ProratedCalculationData calculateProratedData() {
        BigDecimal proratedFee = chargeItem.getPrice(additionalBillingFactors)
                .multiply(BigDecimal.valueOf(this.getUsageDays()))
                .multiply(calculateSuspensionRatio())
                .divide(BigDecimal.valueOf(this.getDayOfMonth()), 5, RoundingMode.HALF_UP);
        BigDecimal balance = new BigDecimal(proratedFee.unscaledValue(), proratedFee.scale());

        return new ProratedCalculationData(
            this.contractWithProductsAndSuspensions.getContractId(),
            productOffering.getProductOfferingId(),
            chargeItem.getChargeItemId(),
            chargeItem.getRevenueItemId(),
            getStartDate(),
            getEndDate(),
            suspension.map(Suspension::getSuspensionType),
            proratedFee,
            balance
        );
    }
}