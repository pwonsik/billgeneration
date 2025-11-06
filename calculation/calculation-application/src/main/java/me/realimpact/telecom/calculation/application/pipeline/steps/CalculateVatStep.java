package me.realimpact.telecom.calculation.application.pipeline.steps;

import lombok.RequiredArgsConstructor;
import me.realimpact.telecom.calculation.application.pipeline.CalculationStep;
import me.realimpact.telecom.calculation.application.pipeline.PipelineContext;
import me.realimpact.telecom.calculation.application.vat.VatCalculator;
import me.realimpact.telecom.calculation.domain.CalculationResult;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CalculateVatStep implements CalculationStep {
    private final VatCalculator vatCalculator;

    @Override
    public void execute(PipelineContext context) {
        List<CalculationResult<?>> results = context.getCalculationResults();
        List<CalculationResult<?>> newResults = new ArrayList<>(results);
        newResults.addAll(vatCalculator.calculateVat(context.getCalculationContext(), results));
        context.setCalculationResults(newResults);
    }
}
