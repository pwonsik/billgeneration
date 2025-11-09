package bill.application.pipeline.steps;

import java.util.ArrayList;
import java.util.List;

import bill.application.pipeline.CalculationStep;
import bill.application.pipeline.PipelineContext;
import bill.application.vat.VatCalculator;
import bill.domain.CalculationResult;
import lombok.RequiredArgsConstructor;

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
