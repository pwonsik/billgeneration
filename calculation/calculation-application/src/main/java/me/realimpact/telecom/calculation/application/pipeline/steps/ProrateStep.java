package me.realimpact.telecom.calculation.application.pipeline.steps;

import lombok.RequiredArgsConstructor;
import me.realimpact.telecom.calculation.application.discount.CalculationResultProrater;
import me.realimpact.telecom.calculation.application.pipeline.CalculationStep;
import me.realimpact.telecom.calculation.application.pipeline.PipelineContext;
import me.realimpact.telecom.calculation.domain.CalculationResult;

import java.util.ArrayList;
import java.util.List;

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
