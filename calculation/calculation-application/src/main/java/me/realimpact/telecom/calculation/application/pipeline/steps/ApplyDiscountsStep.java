package me.realimpact.telecom.calculation.application.pipeline.steps;

import lombok.RequiredArgsConstructor;
import me.realimpact.telecom.calculation.application.discount.DiscountCalculator;
import me.realimpact.telecom.calculation.application.pipeline.CalculationStep;
import me.realimpact.telecom.calculation.application.pipeline.PipelineContext;
import me.realimpact.telecom.calculation.domain.CalculationResult;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ApplyDiscountsStep implements CalculationStep {
    private final DiscountCalculator discountCalculator;

    @Override
    public void execute(PipelineContext context) {
        List<CalculationResult<?>> results = context.getCalculationResults();
        List<CalculationResult<?>> newResults = new ArrayList<>(results);
        newResults.addAll(discountCalculator.process(
                context.getCalculationContext(),
                results, // Note: DiscountCalculator.process has a side effect of modifying this list
                context.getCalculationTarget().discounts()
        ));
        context.setCalculationResults(newResults);
    }
}
