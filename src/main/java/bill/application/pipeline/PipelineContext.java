package bill.application.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

import bill.domain.CalculationContext;
import bill.domain.CalculationResult;
import bill.domain.CalculationTarget;

@Getter
@Setter
@RequiredArgsConstructor
public class PipelineContext {
    private final CalculationContext calculationContext;
    private final CalculationTarget calculationTarget;
    private List<CalculationResult<?>> calculationResults;
}
