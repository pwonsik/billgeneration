package wirelessbill.application.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import wirelessbill.application.discount.CalculationResultProrater;
import wirelessbill.application.discount.DiscountCalculator;
import wirelessbill.application.monthlyfee.MonthlyFeeCalculator;
import wirelessbill.application.onetimecharge.OneTimeChargeCalculator;
import wirelessbill.application.pipeline.steps.ApplyDiscountsStep;
import wirelessbill.application.pipeline.steps.CalculateVatStep;
import wirelessbill.application.pipeline.steps.ConsolidateStep;
import wirelessbill.application.pipeline.steps.InitializeStep;
import wirelessbill.application.pipeline.steps.ProrateStep;
import wirelessbill.application.vat.VatCalculator;
import wirelessbill.domain.monthlyfee.MonthlyChargeDomain;
import wirelessbill.domain.onetimecharge.OneTimeChargeDomain;

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
