package me.realimpact.telecom.calculation.application.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.realimpact.telecom.calculation.application.discount.CalculationResultProrater;
import me.realimpact.telecom.calculation.application.discount.DiscountCalculator;
import me.realimpact.telecom.calculation.application.monthlyfee.MonthlyFeeCalculator;
import me.realimpact.telecom.calculation.application.onetimecharge.OneTimeChargeCalculator;
import me.realimpact.telecom.calculation.application.pipeline.steps.ApplyDiscountsStep;
import me.realimpact.telecom.calculation.application.pipeline.steps.CalculateVatStep;
import me.realimpact.telecom.calculation.application.pipeline.steps.ConsolidateStep;
import me.realimpact.telecom.calculation.application.pipeline.steps.InitializeStep;
import me.realimpact.telecom.calculation.application.pipeline.steps.ProrateStep;
import me.realimpact.telecom.calculation.application.vat.VatCalculator;
import me.realimpact.telecom.calculation.domain.monthlyfee.MonthlyChargeDomain;
import me.realimpact.telecom.calculation.domain.onetimecharge.OneTimeChargeDomain;

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
