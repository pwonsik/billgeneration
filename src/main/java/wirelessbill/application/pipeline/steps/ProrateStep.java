package wirelessbill.application.pipeline.steps;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import wirelessbill.application.discount.CalculationResultProrater;
import wirelessbill.application.pipeline.CalculationStep;
import wirelessbill.application.pipeline.PipelineContext;
import wirelessbill.domain.CalculationResult;

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
