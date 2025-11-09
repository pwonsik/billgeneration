package wirelessbill.application.pipeline.steps;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import wirelessbill.application.discount.DiscountCalculator;
import wirelessbill.application.pipeline.CalculationStep;
import wirelessbill.application.pipeline.PipelineContext;
import wirelessbill.domain.CalculationResult;

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
