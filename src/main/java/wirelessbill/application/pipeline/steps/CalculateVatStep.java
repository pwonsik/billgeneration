package wirelessbill.application.pipeline.steps;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import wirelessbill.application.pipeline.CalculationStep;
import wirelessbill.application.pipeline.PipelineContext;
import wirelessbill.application.vat.VatCalculator;
import wirelessbill.domain.CalculationResult;

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
