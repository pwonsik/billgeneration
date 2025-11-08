package bill.application.pipeline.steps;

import java.util.ArrayList;
import java.util.List;

import bill.application.discount.CalculationResultProrater;
import bill.application.pipeline.CalculationStep;
import bill.application.pipeline.PipelineContext;
import bill.domain.CalculationResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProrateStep implements CalculationStep {
    private final CalculationResultProrater prorater;

    @Override
    public void execute(PipelineContext context) {
        List<CalculationResult<?>> proratedResults = new ArrayList<>(prorater.prorate(
                context.getCalculationContext(),
                context.getCalculationResults(),
                context.getCalculationTarget().discounts()
        ));
        context.setCalculationResults(proratedResults);
    }
}
