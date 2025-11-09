package bill.application.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import bill.application.discount.CalculationResultProrater;
import bill.application.discount.DiscountCalculator;
import bill.application.monthlyfee.MonthlyFeeCalculator;
import bill.application.onetimecharge.OneTimeChargeCalculator;
import bill.application.pipeline.steps.ApplyDiscountsStep;
import bill.application.pipeline.steps.CalculateVatStep;
import bill.application.pipeline.steps.ConsolidateStep;
import bill.application.pipeline.steps.InitializeStep;
import bill.application.pipeline.steps.ProrateStep;
import bill.application.vat.VatCalculator;
import bill.domain.monthlyfee.MonthlyChargeDomain;
import bill.domain.onetimecharge.OneTimeChargeDomain;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultCalculationPipeline implements CalculationPipeline {
    private final List<MonthlyFeeCalculator<? extends MonthlyChargeDomain>> monthlyFeeCalculators;
    private final List<OneTimeChargeCalculator<? extends OneTimeChargeDomain>> oneTimeChargeCalculators;
    private final CalculationResultProrater prorater;
    private final DiscountCalculator discountCalculator;
    private final VatCalculator vatCalculator;

    private List<CalculationStep> steps;

    @PostConstruct
    public void init() {
        steps = new ArrayList<>();
        steps.add(new InitializeStep(monthlyFeeCalculators, oneTimeChargeCalculators));
        steps.add(new ProrateStep(prorater));
        steps.add(new ApplyDiscountsStep(discountCalculator));
        steps.add(new ConsolidateStep(prorater));
        steps.add(new CalculateVatStep(vatCalculator));
    }

    @Override
    public List<CalculationStep> getSteps() {
        return steps;
    }
}
